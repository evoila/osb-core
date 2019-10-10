package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.*;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.BindingService;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.util.ParameterValidator;
import de.evoila.cf.security.utils.RandomString;
import org.everit.json.schema.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author Johannes Hiemer, Marco Di Martino.
 **/
@Service
public abstract class BindingServiceImpl implements BindingService {

	private final Logger log = LoggerFactory.getLogger(BindingServiceImpl.class);

	protected BindingRepository bindingRepository;

	protected ServiceDefinitionRepository serviceDefinitionRepository;

	protected ServiceInstanceRepository serviceInstanceRepository;

	protected RouteBindingRepository routeBindingRepository;

	protected JobRepository jobRepository;

	protected AsyncBindingService asyncBindingService;

	protected PlatformRepository platformRepository;

	private RandomString randomString = new RandomString();


	public BindingServiceImpl(BindingRepository bindingRepository, ServiceDefinitionRepository serviceDefinitionRepository,
							  ServiceInstanceRepository serviceInstanceRepository, RouteBindingRepository routeBindingRepository,
							  JobRepository jobRepository, AsyncBindingService asyncBindingService, PlatformRepository platformRepository) {
		this.bindingRepository = bindingRepository;
		this.serviceDefinitionRepository = serviceDefinitionRepository;
		this.serviceInstanceRepository = serviceInstanceRepository;
		this.routeBindingRepository = routeBindingRepository;
		this.jobRepository = jobRepository;
		this.asyncBindingService = asyncBindingService;
		this.platformRepository = platformRepository;
	}

	@Override
	public BaseServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			ServiceInstanceBindingRequest serviceInstanceBindingRequest, boolean async) throws ServiceInstanceBindingExistsException,
			ServiceBrokerException, ServiceDefinitionDoesNotExistException, ServiceInstanceDoesNotExistException,
			InvalidParametersException, AsyncRequiredException, ValidationException, PlatformException  {

		validateBindingNotExists(serviceInstanceBindingRequest, bindingId, instanceId);

		ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);

		Plan plan = serviceDefinitionRepository.getPlan(serviceInstanceBindingRequest.getPlanId());
		if (serviceInstanceBindingRequest.getParameters() != null) {
		    ParameterValidator.validateParameters(serviceInstanceBindingRequest, plan, false);
		}

		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		BaseServiceInstanceBindingResponse baseServiceInstanceBindingResponse;
        String operationId = randomString.nextString();

        if (platformService.isSyncPossibleOnBind()) {
			baseServiceInstanceBindingResponse = syncCreateBinding(bindingId, serviceInstance,
					serviceInstanceBindingRequest, plan);
		} else {
        	if (!async) {
				throw new AsyncRequiredException();
			}

			bindingRepository.addInternalBinding(new ServiceInstanceBinding(bindingId, instanceId, null));

			asyncBindingService.asyncCreateServiceInstanceBinding(this, bindingId,
					serviceInstance, serviceInstanceBindingRequest, plan, true, operationId);

			baseServiceInstanceBindingResponse = new ServiceInstanceBindingOperationResponse(operationId);
		}

		return baseServiceInstanceBindingResponse;
	}

	protected abstract RouteBinding bindRoute(ServiceInstance serviceInstance, String route);

	protected ServiceInstanceBinding createServiceInstanceBinding(String bindingId, String serviceInstanceId,
																  Map<String, Object> credentials, String syslogDrainUrl, String appGuid) {
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId, serviceInstanceId, credentials,
				syslogDrainUrl);
		return binding;
	}

	@Override
	public BaseServiceInstanceBindingResponse deleteServiceInstanceBinding(String bindingId, String planId, boolean async)
			throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException,
            AsyncRequiredException {
		ServiceInstance serviceInstance = getBinding(bindingId);
		Plan plan = serviceDefinitionRepository.getPlan(planId);
		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());
		String operationId = randomString.nextString();

		if (platformService.isSyncPossibleOnUnbind()){
			syncDeleteServiceInstanceBinding(bindingId, serviceInstance, plan);
		} else {
			if (!async){
				throw new AsyncRequiredException();
			}

			asyncBindingService.asyncDeleteServiceInstanceBinding(this, bindingId, serviceInstance,
					plan, operationId);
		}

		return new ServiceInstanceBindingOperationResponse(operationId);
	}

	@Override
	public ServiceInstanceBinding fetchServiceInstanceBinding(String bindingId, String instanceId) throws ServiceInstanceBindingNotFoundException {

		ServiceInstanceBinding serviceInstanceBinding;
		try {
			serviceInstanceBinding = bindingRepository.findOne(bindingId);
		} catch(Exception e) {
			throw new ServiceInstanceBindingNotFoundException();
		}

		boolean isBindingInProgress = false;
		if (jobRepository.containsJobProgress(bindingId)){
			JobProgress job = jobRepository.getJobProgressByReferenceId(bindingId);
			isBindingInProgress = job.getOperation().equals(JobProgress.BIND) && job.getState().equals(JobProgress.IN_PROGRESS);
		}

		if (isBindingInProgress) {
			throw new ServiceInstanceBindingNotFoundException();
		}
		return serviceInstanceBinding;
	}

	@Override
	public JobProgressResponse getLastOperationByReferenceId(String referenceId)
			throws  ServiceInstanceBindingDoesNotExistsException {
		JobProgress progress = asyncBindingService.getProgressByReferenceId(referenceId);

		if (progress == null || !bindingRepository.containsInternalBindingId(referenceId)) {
			throw new ServiceInstanceBindingDoesNotExistsException(referenceId);
		}

		return new JobProgressResponse(progress);
	}

    @Override
    public JobProgressResponse getLastOperationById(String referenceId, String jobProgressId)
            throws  ServiceInstanceBindingDoesNotExistsException {
        JobProgress progress = asyncBindingService.getProgressById(jobProgressId);

        if (progress == null || !bindingRepository.containsInternalBindingId(referenceId)) {
            throw new ServiceInstanceBindingDoesNotExistsException(referenceId);
        }

        return new JobProgressResponse(progress);
    }

	public ServiceInstanceBindingResponse syncCreateBinding(String bindingId, ServiceInstance serviceInstance,
                                                            ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                            Plan plan)
			throws ServiceBrokerException, InvalidParametersException, PlatformException{
		ServiceInstanceBindingResponse  response = createBinding(bindingId, serviceInstance, serviceInstanceBindingRequest, plan);
		jobRepository.saveJobProgress(randomString.nextString(), bindingId, JobProgress.SUCCESS, "Successfully created synchronous binding.", JobProgress.BIND);

		return response;
	}

	public ServiceInstanceBindingResponse createBinding(String bindingId, ServiceInstance serviceInstance,
														ServiceInstanceBindingRequest serviceInstanceBindingRequest,
														Plan plan)
			throws ServiceBrokerException, InvalidParametersException, PlatformException {

		ServiceInstanceBindingResponse serviceInstanceBindingResponse;
		if (serviceInstanceBindingRequest.getBindResource() != null && !StringUtils
				.isEmpty(serviceInstanceBindingRequest.getBindResource().getRoute())) {
			RouteBinding routeBinding = bindRoute(serviceInstance, serviceInstanceBindingRequest.getBindResource().getRoute());
			routeBindingRepository.addRouteBinding(routeBinding);
			serviceInstanceBindingResponse = new ServiceInstanceBindingResponse(routeBinding.getRoute());

			return serviceInstanceBindingResponse;
		}

		ServiceInstanceBinding binding = bindService(bindingId, serviceInstanceBindingRequest, serviceInstance, plan);

		bindingRepository.addInternalBinding(binding);
		serviceInstanceBindingResponse = new ServiceInstanceBindingResponse(binding);

		return serviceInstanceBindingResponse;
	}


	public void syncDeleteServiceInstanceBinding(String bindingId, ServiceInstance serviceInstance, Plan plan) {
	    deleteServiceInstanceBinding(bindingId, serviceInstance, plan);
        jobRepository.deleteJobProgressByReferenceId(bindingId);
    }

	public void deleteServiceInstanceBinding(String bindingId, ServiceInstance serviceInstance, Plan plan){
        try {
            ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
            unbindService(binding, serviceInstance, plan);
        } catch (ServiceBrokerException | PlatformException e) {
            log.error("Could not cleanup service binding", e);
        } finally {
            bindingRepository.unbindService(bindingId);
        }
    }

	protected ServiceInstance getBinding(String bindingId) throws ServiceInstanceBindingDoesNotExistsException {
		if (!bindingRepository.containsInternalBindingId(bindingId)) {
			throw new ServiceInstanceBindingDoesNotExistsException(bindingId);
		}
		String serviceInstanceId = bindingRepository.getInternalBindingId(bindingId);
		if (serviceInstanceId == null) {
			throw new ServiceInstanceBindingDoesNotExistsException(bindingId);
		}
		ServiceInstance serviceInstance;
		try {
			serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
		} catch (ServiceInstanceDoesNotExistException e) {
			log.error("Service Instance does not exist!", e);
			throw new ServiceInstanceBindingDoesNotExistsException(bindingId);
		}

		return serviceInstance;
	}

    protected ServiceInstanceBinding bindService(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                 ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException, InvalidParametersException, PlatformException {
        Map<String, Object> credentials = createCredentials(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, null);
        String appGuid = getAppGuidFromBindingRequest(serviceInstanceBindingRequest);
        ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials);
        binding.setAppGuid(appGuid);

		return binding;
	}

	protected abstract void unbindService(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException, PlatformException;

	protected abstract Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                             ServiceInstance serviceInstance, Plan plan,
                                                             ServerAddress serverAddress) throws ServiceBrokerException, InvalidParametersException, PlatformException;

    protected void validateBindingNotExists(ServiceInstanceBindingRequest serviceInstanceBindingRequest, String bindingId, String instanceId)
            throws ServiceInstanceBindingExistsException, ServiceInstanceDoesNotExistException {

		if (bindingRepository.containsInternalBindingId(bindingId)) {
			boolean bindCreation;
			boolean isBindingInProgress;

			try {
				JobProgress jobProgress = jobRepository.getJobProgressByReferenceId(bindingId);
				bindCreation = jobProgress.isBinding();
				isBindingInProgress = jobProgress.isInProgress();
			} catch (NoSuchElementException e) {
				return;
			}
            if (bindCreation && !isBindingInProgress) {
                ServiceInstanceBinding serviceInstanceBinding = bindingRepository.findOne(bindingId);
                boolean identical = wouldCreateIdenticalBinding(serviceInstanceBindingRequest, serviceInstanceBinding);

                throw new ServiceInstanceBindingExistsException(bindingId, instanceId, identical);
            }
        }
    }

	public ServiceInstance getServiceInstance(String instanceId) throws ServiceInstanceDoesNotExistException {
		return serviceInstanceRepository.getServiceInstance(instanceId);
	}

	private boolean wouldCreateIdenticalBinding(ServiceInstanceBindingRequest request, ServiceInstanceBinding serviceInstanceBinding) throws ServiceInstanceDoesNotExistException {
		ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceBinding.getServiceInstanceId());
		String routeBinding = getRouteBindingFromInstanceBinding(serviceInstanceBinding.getId());
		String requestRouteBinding = getRouteBindingFromBindingRequest(request);
		String requestAppGuid = getAppGuidFromBindingRequest(request);

		return Objects.equals(routeBinding, requestRouteBinding) &&
				Objects.equals(requestAppGuid, serviceInstanceBinding.getAppGuid()) &&
				Objects.equals(request.getServiceDefinitionId(), serviceInstance.getServiceDefinitionId()) &&
				Objects.equals(request.getPlanId(), (serviceInstance.getPlanId())) &&
				Objects.equals(request.getParameters(), serviceInstanceBinding.getParameters());
	}

	private String getAppGuidFromBindingRequest(ServiceInstanceBindingRequest request) {
		String appGuid = request.getAppGuid();
		if (request.getBindResource() != null && request.getBindResource().getAppGuid() != null) {
			appGuid = request.getBindResource().getAppGuid();
		}
		return appGuid;
	}

	private String getRouteBindingFromBindingRequest(ServiceInstanceBindingRequest request) {
		BindResource bindResource = request.getBindResource();

		if (bindResource != null) {
			return bindResource.getRoute();
		}
		return null;
	}

	private String getRouteBindingFromInstanceBinding(String bindingId) {
		if (routeBindingRepository.containsRouteBindingId(bindingId)) {

			return routeBindingRepository.findOne(bindingId).getRoute();
		}
		return null;
	}
}
