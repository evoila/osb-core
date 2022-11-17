
package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.controller.utils.ParameterUtil;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.json.schema.JsonSchema;
import de.evoila.cf.broker.model.json.schema.utils.JsonSchemaUtils;
import de.evoila.cf.broker.repository.*;
import de.evoila.cf.broker.service.*;
import de.evoila.cf.broker.util.ParameterValidator;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import de.evoila.cf.security.utils.RandomString;
import org.everit.json.schema.SchemaException;
import org.everit.json.schema.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Christian Brinker, Marco Di Martino, Johannes Hiemer.
 **/
@Service
public class DeploymentServiceImpl implements DeploymentService {

    private Logger log = LoggerFactory.getLogger(getClass());

    private PlatformRepository platformRepository;

    private ServiceDefinitionRepository serviceDefinitionRepository;

    private ServiceInstanceRepository serviceInstanceRepository;

    private BindingRepository bindingRepository;

    private JobRepository jobRepository;

    private CatalogService catalogService;

    private AsyncDeploymentService asyncDeploymentService;

    private BindingService bindingService;

    private RandomString randomString = new RandomString();

    public DeploymentServiceImpl(PlatformRepository platformRepository,
            ServiceDefinitionRepository serviceDefinitionRepository,
            ServiceInstanceRepository serviceInstanceRepository, JobRepository jobRepository,
            BindingRepository bindingRepository, AsyncDeploymentService asyncDeploymentService,
            BindingService bindingService, CatalogService catalogService) {
        this.platformRepository = platformRepository;
        this.serviceDefinitionRepository = serviceDefinitionRepository;
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.bindingRepository = bindingRepository;
        this.jobRepository = jobRepository;
        this.asyncDeploymentService = asyncDeploymentService;
        this.bindingService = bindingService;
        this.catalogService = catalogService;
    }

    @Override
    public JobProgressResponse getLastOperationByReferenceId(String referenceId)
            throws ServiceInstanceDoesNotExistException {
        JobProgress progress = asyncDeploymentService.getProgressByReferenceId(referenceId);

        if (progress == null || !serviceInstanceRepository.containsServiceInstanceId(referenceId)) {
            throw new ServiceInstanceDoesNotExistException(referenceId);
        }

        return new JobProgressResponse(progress);
    }

    @Override
    public JobProgressResponse getLastOperationById(String serviceInstanceId, String jobProgressId)
            throws ServiceInstanceDoesNotExistException {
        JobProgress progress = asyncDeploymentService.getProgressById(jobProgressId);

        if (progress == null || !serviceInstanceRepository.containsServiceInstanceId(serviceInstanceId)) {
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }

        return new JobProgressResponse(progress);
    }

    @Override
    public ServiceInstanceOperationResponse createServiceInstance(String serviceInstanceId,
            ServiceInstanceRequest request) throws ServiceInstanceExistsException, ServiceBrokerException,
            ServiceDefinitionDoesNotExistException, ValidationException, ServiceDefinitionPlanDoesNotExistException {
        log.debug("Inside createServiceInstance (DeploymentServiceImpl)");
        serviceDefinitionRepository.validateServiceId(request.getServiceDefinitionId());
        Optional<ServiceInstance> serviceInstanceOptional = serviceInstanceRepository
                .getServiceInstanceOptional(serviceInstanceId);

        if (serviceInstanceOptional.isPresent()) {
            JobProgress jobProgress = jobRepository.getJobProgressByReferenceId(serviceInstanceId);
            if (jobProgress != null && jobProgress.isProvisioning()) {
                if (jobProgress.isInProgress()) {
                    return new ServiceInstanceOperationResponse(jobProgress.getId(),
                            serviceInstanceOptional.get().getDashboardUrl(), true);
                } else if (jobProgress.isSucceeded() && ServiceInstanceUtils
                        .wouldCreateIdenticalInstance(serviceInstanceId, request, serviceInstanceOptional.get())) {
                    throw new ServiceInstanceExistsException(serviceInstanceId, request.getServiceDefinitionId(), true,
                            new ServiceInstanceOperationResponse(jobProgress.getId(),
                                    serviceInstanceOptional.get().getDashboardUrl(), true));
                }
            }
            throw new ServiceInstanceExistsException(serviceInstanceId, request.getServiceDefinitionId());
        }

        ServiceDefinition serviceDefinition = catalogService.getServiceDefinition(request.getServiceDefinitionId());
        ServiceInstance serviceInstance = new ServiceInstance(serviceInstanceId, request.getServiceDefinitionId(),
                request.getPlanId(), request.getOrganizationGuid(), request.getSpaceGuid(), request.getParameters(),
                request.getContext());
        serviceInstance.setAllowContextUpdates(serviceDefinition.isAllowContextUpdates());

        ServiceInstanceOperationResponse serviceInstanceOperationResponse = new ServiceInstanceOperationResponse();

        if (DashboardUtils.hasDashboard(serviceDefinition)) {
            String dashboardUrl = DashboardUtils.dashboard(serviceDefinition, serviceInstanceId);
            serviceInstance.setDashboardUrl(dashboardUrl);
            serviceInstanceOperationResponse.setDashboardUrl(dashboardUrl);
        }

        Plan plan = serviceDefinitionRepository.getPlan(request.getServiceDefinitionId(), request.getPlanId());

        if (request.getParameters() == null) {
            request.setParameters(new HashMap<String, Object>());
        }

        JsonSchema jsonSchema = ParameterUtil.resolve(() -> plan.getSchemas().getServiceInstance().getCreate().getParameters()).orElse(null);
        if (jsonSchema != null) {
                try {
                    JsonSchemaUtils.defaults(jsonSchema, request.getParameters());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new SchemaException(e.getMessage());
                }
        }


        ParameterValidator.validateParameters(request, plan, false);

        PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

        if (platformService == null) {
            throw new ServiceBrokerException("No Platform configured for " + plan.getPlatform());
        }

        if (platformService.isSyncPossibleOnCreate(plan)) {
            return serviceInstanceOperationResponse;
        } else {
            serviceInstanceRepository.saveServiceInstance(serviceInstance);

            String jobProgressId = randomString.nextString();
            log.debug("Inside createServiceInstance before asyncCreateInstance");
            asyncDeploymentService.asyncCreateInstance(this, serviceInstance, request.getParameters(), plan,
                    platformService, jobProgressId);

            serviceInstanceOperationResponse.setOperation(jobProgressId);
            serviceInstanceOperationResponse.setAsync(true);

            return serviceInstanceOperationResponse;
        }
    }

    @Override
    public ServiceInstanceOperationResponse updateServiceInstance(String serviceInstanceId,
            ServiceInstanceUpdateRequest request) throws ServiceBrokerException, ServiceInstanceDoesNotExistException,
            ServiceDefinitionDoesNotExistException, ServiceDefinitionPlanDoesNotExistException, ValidationException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
        Plan plan = serviceDefinitionRepository.getPlan(request.getServiceDefinitionId(), request.getPlanId());

        if (request.getParameters() == null) {
            request.setParameters(new HashMap<String, Object>());
        }

        JsonSchema jsonSchema = ParameterUtil.resolve(() -> plan.getSchemas().getServiceInstance().getUpdate().getParameters()).orElse(null);
        if (jsonSchema != null) {
                try{
                    JsonSchemaUtils.defaults(jsonSchema, request.getParameters());
                } catch (Exception e) {
                    throw new SchemaException(e.getMessage(),e);
                }
            }

            ParameterValidator.validateParameters(request, plan, true);


        PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

        if (platformService == null) {
            throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
        }

        ServiceInstanceOperationResponse serviceInstanceOperationResponse = new ServiceInstanceOperationResponse();
        if (platformService.isSyncPossibleOnUpdate(serviceInstance, plan)) {
            syncUpdateInstance(serviceInstance, request.getParameters(), plan, platformService);
        } else {
            String jobProgressId = randomString.nextString();
            asyncDeploymentService.asyncUpdateInstance(this, serviceInstance, request.getParameters(), plan,
                    platformService, jobProgressId);

            serviceInstanceOperationResponse.setOperation(jobProgressId);
            serviceInstanceOperationResponse.setAsync(true);

            return serviceInstanceOperationResponse;
        }

        return serviceInstanceOperationResponse;
    }

    @Override
    public ServiceInstanceOperationResponse updateServiceInstanceContext(String serviceInstanceId,
            ServiceInstanceUpdateRequest serviceInstanceUpdateRequest) throws ServiceInstanceDoesNotExistException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
        serviceInstance.setContext(serviceInstanceUpdateRequest.getContext());
        serviceInstanceRepository.updateServiceInstance(serviceInstance);

        return new ServiceInstanceOperationResponse();
    }

    @Override
    public ServiceInstanceOperationResponse deleteServiceInstance(String instanceId)
            throws ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException,
            ServiceDefinitionPlanDoesNotExistException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);
        Plan plan = serviceDefinitionRepository.getPlan(serviceInstance.getServiceDefinitionId(),
                serviceInstance.getPlanId());
        PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

        if (platformService == null) {
            throw new ServiceBrokerException("No Platform configured for " + plan.getPlatform());
        }

        boolean isSyncPossible = platformService.isSyncPossibleOnDelete(serviceInstance);

        List<ServiceInstanceBinding> bindings = bindingRepository.getBindingsForServiceInstance(instanceId);
        bindings.forEach(
                serviceInstanceBinding -> deleteBindings(serviceInstanceBinding, serviceInstance, !isSyncPossible));

        ServiceInstanceOperationResponse serviceInstanceOperationResponse = new ServiceInstanceOperationResponse();

        // Will cause a NPE, if there is no JobProgress object. To fix this, the
        // JobRepository has to be touched.
        JobProgress jobProgress = jobRepository.getJobProgressByReferenceId(instanceId);
        if (jobProgress != null && jobProgress.isDeleting() && jobProgress.isInProgress()) {
            serviceInstanceOperationResponse.setOperation(jobProgress.getId());
            serviceInstanceOperationResponse.setAsync(true);
            return serviceInstanceOperationResponse;
        }

        if (isSyncPossible) {
            syncDeleteInstance(serviceInstance, plan, platformService);
        } else {
            String jobProgressId = randomString.nextString();
            asyncDeploymentService.asyncDeleteInstance(this, serviceInstance, plan, platformService, jobProgressId);
            serviceInstanceOperationResponse.setOperation(jobProgressId);
            serviceInstanceOperationResponse.setAsync(true);
        }

        return serviceInstanceOperationResponse;
    }

    private void deleteBindings(ServiceInstanceBinding serviceInstanceBinding, ServiceInstance serviceInstance,
            boolean async) {
        try {
            bindingService.deleteServiceInstanceBinding(serviceInstanceBinding.getId(),
                    serviceInstance.getServiceDefinitionId(), serviceInstance.getPlanId(), async);
        } catch (Exception e) {
            log.error("Error while Deleting Binding ", e);
        }
    }

    @Override
    public ServiceInstance fetchServiceInstance(String instanceId)
            throws UnsupportedOperationException, ConcurrencyErrorException, ServiceInstanceNotFoundException {

        ServiceInstance serviceInstance;
        try {
            serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);

            Plan plan = serviceDefinitionRepository.getPlan(serviceInstance.getServiceDefinitionId(),
                    serviceInstance.getPlanId());

            PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

            platformService.getInstance(serviceInstance, plan);
        } catch (Exception e) {
            throw new ServiceInstanceNotFoundException();
        }

        if (jobRepository.containsJobProgress(instanceId)) {
            JobProgress job = jobRepository.getJobProgressByReferenceId(instanceId);
            if (job.getOperation().equals(JobProgress.PROVISION) && job.getState().equals(JobProgress.IN_PROGRESS)) {
                throw new ServiceInstanceNotFoundException();
            } else if (job.getOperation().equals(JobProgress.UPDATE)
                    && job.getState().equals(JobProgress.IN_PROGRESS)) {
                throw new ConcurrencyErrorException("Service Instance");
            }
        }
        return serviceInstance;

    }

    public ServiceInstance syncCreateInstance(ServiceInstance serviceInstance, Map<String, Object> parameters,
            Plan plan, PlatformService platformService) throws ServiceBrokerException {
        // TODO: We need to decide which method we trigger when preCreateInstance fails
        log.debug("Inside syncCreateInstance");
        try {
            serviceInstance = platformService.preCreateInstance(serviceInstance, plan);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during pre service instance creation", e);
        }

        try {
            serviceInstance = platformService.createInstance(serviceInstance, plan, parameters);
        } catch (PlatformException | ServiceDefinitionDoesNotExistException e) {
            log.error("Could not create instance due to: ", e);
            throw new ServiceBrokerException("Could not create instance due to: ", e);
        }

        try {
            serviceInstance = platformService.postCreateInstance(serviceInstance, plan);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during post service instance creation", e);
        }

        serviceInstanceRepository.saveServiceInstance(serviceInstance);

        return serviceInstance;
    }

    public ServiceInstance syncUpdateInstance(ServiceInstance serviceInstance, Map<String, Object> parameters,
            Plan plan, PlatformService platformService)
            throws ServiceBrokerException, ServiceDefinitionDoesNotExistException {

        // TODO: We need to decide which method we trigger when preCreateInstance fails
        try {
            serviceInstance = platformService.preUpdateInstance(serviceInstance, plan);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during pre service instance update", e);
        }

        try {
            serviceInstance = platformService.updateInstance(serviceInstance, plan, parameters);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Could not update instance due to: ", e);
        }

        try {
            serviceInstance = platformService.postUpdateInstance(serviceInstance, plan);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during post service instance update", e);
        }

        serviceInstanceRepository.updateServiceInstance(serviceInstance);

        return serviceInstance;
    }

    public void syncDeleteInstance(ServiceInstance serviceInstance, Plan plan, PlatformService platformService)
            throws ServiceBrokerException {

        try {
            platformService.preDeleteInstance(serviceInstance);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during pre service instance deletion", e);
        }

        try {
            platformService.deleteInstance(serviceInstance, plan);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during deletion of service", e);
        }

        try {
            platformService.postDeleteInstance(serviceInstance);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during pre service instance deletion", e);
        }

        serviceInstanceRepository.deleteServiceInstance(serviceInstance.getId());
        jobRepository.deleteJobProgressByReferenceId(serviceInstance.getId());
    }

    public void updateInstanceInfo(ServiceInstance serviceInstance) {
        serviceInstanceRepository.updateServiceInstance(serviceInstance);
    }


}
