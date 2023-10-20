package de.evoila.cf.broker.controller.core;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.annotations.ApiVersion;
import de.evoila.cf.broker.model.catalog.MaintenanceInfo;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.DeploymentService;
import de.evoila.cf.broker.util.EmptyRestResponse;
import de.evoila.cf.broker.controller.utils.JobProgressUtils;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import de.evoila.cf.broker.util.UuidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

/**
 * @author Johannes Hiemer, Christian Brinker, Marco Di Martino, Johannes Strauß.
 **/
@RestController
@RequestMapping(value = "/v2/service_instances")
public class ServiceInstanceController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(ServiceInstanceController.class);

    private static final String SERVICE_INSTANCE_BASE_PATH = "/v2/service_instances";

    private DeploymentService deploymentService;

    private EndpointConfiguration endpointConfiguration;

    private CatalogService catalogService;

    private ServiceInstanceRepository serviceInstanceRepository;

    private ServiceInstanceUtils serviceInstanceUtils;

    private JobProgressUtils jobProgressUtils;

    public ServiceInstanceController(DeploymentService deploymentService,
                                     EndpointConfiguration endpointConfiguration,
                                     CatalogService catalogService,
                                     ServiceInstanceRepository serviceInstanceRepository,
                                     ServiceInstanceUtils serviceInstanceUtils,
                                     JobProgressUtils jobProgressUtils) {
        this.deploymentService = deploymentService;
        this.endpointConfiguration = endpointConfiguration;
        this.catalogService = catalogService;
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.serviceInstanceUtils = serviceInstanceUtils;
        this.jobProgressUtils = jobProgressUtils;
    }

    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214, ApiVersions.API_215})
    @PutMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<ServiceInstanceOperationResponse> create(
            @PathVariable
            @Pattern(regexp = UuidUtils.UUID_REGEX, message = UuidUtils.NOT_A_UUID_MESSAGE) String serviceInstanceId,
            @RequestParam(value = "accepts_incomplete", required = false, defaultValue = "false") Boolean acceptsIncomplete,
            @Valid @RequestBody ServiceInstanceRequest request,
            @RequestHeader(value = "X-Broker-API-Originating-Identity", required = false) String originatingIdentity,
            @RequestHeader(value = "X-Broker-API-Request-Identity", required = false) String requestIdentity)
            throws ServiceDefinitionDoesNotExistException, ServiceInstanceExistsException, ServiceBrokerException,
            AsyncRequiredException, MaintenanceInfoVersionsDontMatchException, ServiceDefinitionPlanDoesNotExistException {

        if (!acceptsIncomplete) {
            throw new AsyncRequiredException();
        }

        log.debug("PUT: " + SERVICE_INSTANCE_BASE_PATH + "/{serviceInstanceId}"
                + ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId);
        ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());

        if (svc == null) {
            throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
        }

        checkMaintenanceInfo(request);
        ServiceInstanceOperationResponse response = deploymentService.createServiceInstance(serviceInstanceId, request);

        log.debug("ServiceInstance Creation Started: " + serviceInstanceId);

        if (response.isAsync())
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        else
            return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private void checkMaintenanceInfo(BaseServiceInstanceRequest request)
            throws ServiceDefinitionPlanDoesNotExistException, MaintenanceInfoVersionsDontMatchException, ServiceDefinitionDoesNotExistException {
        ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());
        Plan plan = svc.getPlans().stream().filter(planInStream -> request.getPlanId().equals(planInStream.getId()))
                .findFirst().orElseThrow(() -> new ServiceDefinitionPlanDoesNotExistException(request.getServiceDefinitionId(), request.getPlanId()));

        MaintenanceInfo requestInfo = request.getMaintenanceInfo();
        MaintenanceInfo planInfo = plan.getMaintenanceInfo();
        if (requestInfo != null && planInfo == null
                ||
                requestInfo != null && !requestInfo.getVersion().equals(planInfo.getVersion())) {
            throw new MaintenanceInfoVersionsDontMatchException(requestInfo, planInfo);
        }
    }

    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214, ApiVersions.API_215})
    @PatchMapping(value = "/{serviceInstanceId}")
    public ResponseEntity update(
            @PathVariable String serviceInstanceId,
            @RequestParam(value = "accepts_incomplete", required = false, defaultValue = "false") Boolean acceptsIncomplete,
            @RequestBody ServiceInstanceUpdateRequest request,
            @RequestHeader(value = "X-Broker-API-Originating-Identity", required = false) String originatingIdentity,
            @RequestHeader(value = "X-Broker-API-Request-Identity", required = false) String requestIdentity
    ) throws ServiceBrokerException, ServiceDefinitionDoesNotExistException, AsyncRequiredException,
            MaintenanceInfoVersionsDontMatchException, ServiceDefinitionPlanDoesNotExistException, ServiceInstanceNotFoundException, ConcurrencyErrorException {

        if (request.getServiceDefinitionId() == null) {
            return new ResponseEntity<>("Missing required fields: service_id", HttpStatus.BAD_REQUEST);
        }
        checkMaintenanceInfo(request);

        log.debug("PATCH: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
                + ", updateServiceInstance(), serviceInstanceId = " + serviceInstanceId);

        if (!acceptsIncomplete) {
            throw new AsyncRequiredException();
        }

        ServiceInstanceOperationResponse serviceInstanceOperationResponse;
        try {
            ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
            ServiceDefinition serviceDefinition = catalogService.getServiceDefinition(request.getServiceDefinitionId());

            if (serviceInstanceUtils.isBlocked(serviceInstance, JobProgress.UPDATE)) {
                throw new ConcurrencyErrorException("Service Instance");
            }

            if (serviceDefinition.isPlanUpdatable(serviceInstance.getPlanId())) {
                if (!ServiceInstanceUtils.isEffectivelyUpdating(serviceInstance, request)) {
                    log.info("Update would have not effective changes.");
                    return processEmptyErrorResponse(HttpStatus.OK);
                }
                if (request.isContextUpdate()) {
                    if (serviceInstance.isAllowContextUpdates()) {
                        serviceInstanceOperationResponse = deploymentService.updateServiceInstanceContext(serviceInstanceId, request);
                        return new ResponseEntity<>(serviceInstanceOperationResponse, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new ServiceBrokerErrorResponse("ContextUpdateNotAllowed",
                                "It is not allowed to alter the context of the requested service instance"),
                                HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                }
                serviceInstanceOperationResponse = deploymentService.updateServiceInstance(serviceInstanceId, request);
            } else {
                return new ResponseEntity<>(new ServiceBrokerErrorResponse("NotUpdatable", "An update on the requested service instance is not supported."), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } catch (ServiceInstanceDoesNotExistException e) {
            log.error("Service Instance has not been found!", e);
            throw new ServiceInstanceNotFoundException();
        }

        return new ResponseEntity<>(serviceInstanceOperationResponse, HttpStatus.ACCEPTED);
    }

    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214, ApiVersions.API_215})
    @DeleteMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<ServiceInstanceOperationResponse> delete(
            @RequestHeader(value = "X-Broker-API-Originating-Identity", required = false) String originatingIdentity,
            @RequestHeader(value = "X-Broker-API-Request-Identity", required = false) String requestIdentity,
            @PathVariable String serviceInstanceId,
            @RequestParam(value = "accepts_incomplete", required = false, defaultValue = "false") Boolean acceptsIncomplete,
            @RequestParam("service_id") String serviceId, @RequestParam("plan_id") String planId) throws ServiceBrokerException, AsyncRequiredException,
            ServiceDefinitionDoesNotExistException, ServiceInstanceDoesNotExistException, ConcurrencyErrorException, ServiceDefinitionPlanDoesNotExistException {

        log.debug("DELETE: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
                + ", deleteServiceInstanceBinding(), serviceInstanceId = " + serviceInstanceId + ", serviceId = " + serviceId
                + ", planId = " + planId);

        if (!acceptsIncomplete) {
            throw new AsyncRequiredException();
        }

        if (serviceInstanceUtils.isBlocked(serviceInstanceId, JobProgress.DELETE)) {
            throw new ConcurrencyErrorException("Service Instance");
        }

        ServiceInstanceOperationResponse serviceInstanceOperationResponse = deploymentService.deleteServiceInstance(serviceInstanceId);

        if (serviceInstanceOperationResponse.isAsync()) {
            log.debug("ServiceInstance deletion started: " + serviceInstanceId);
            return new ResponseEntity<>(serviceInstanceOperationResponse, HttpStatus.ACCEPTED);
        } else {
            log.debug("ServiceInstance Deleted: " + serviceInstanceId);
            return new ResponseEntity(EmptyRestResponse.BODY, HttpStatus.OK);
        }
    }

    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214, ApiVersions.API_215})
    @GetMapping(value = "/{serviceInstanceId}/last_operation")
    public ResponseEntity<JobProgressResponse> lastOperation(
            @RequestHeader(value = "X-Broker-API-Request-Identity", required = false) String requestIdentity,
            @PathVariable String serviceInstanceId,
            @RequestHeader(value = "X-Broker-API-Originating-Identity", required = false) String originatingIdentity,
            @RequestParam(required = false) String operation)
            throws ServiceInstanceDoesNotExistException {

        JobProgressResponse jobProgressResponse;
        if (operation != null)
            jobProgressResponse = deploymentService.getLastOperationById(serviceInstanceId, operation);
        else
            jobProgressResponse = deploymentService.getLastOperationByReferenceId(serviceInstanceId);

        return jobProgressUtils.buildJobProgressResponseEntity(jobProgressResponse);
    }

    @ApiVersion({ApiVersions.API_214, ApiVersions.API_215})
    @GetMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<ServiceInstanceResponse> get(
            @RequestHeader(value = "X-Broker-API-Request-Identity", required = false) String requestIdentity,
            @RequestHeader(value = "X-Broker-API-Originating-Identity", required = false) String originatingIdentity,
            @PathVariable String serviceInstanceId) throws UnsupportedOperationException,
            ServiceBrokerException, ConcurrencyErrorException, ServiceInstanceNotFoundException, ServiceDefinitionDoesNotExistException {

        ServiceInstance serviceInstance = deploymentService.fetchServiceInstance(serviceInstanceId);

        ServiceDefinition serviceDefinition = catalogService.getServiceDefinition(serviceInstance.getServiceDefinitionId());

        if (!(serviceDefinition.isInstancesRetrievable())) {
            throw new ServiceInstanceNotRetrievableException("The Service Instance is not retrievable. You should not attempt to call this endpoint");
        }
        ServiceInstanceResponse serviceInstanceResponse = new ServiceInstanceResponse(serviceInstance);

        if (DashboardUtils.hasDashboard(serviceDefinition))
            serviceInstanceResponse.setDashboardUrl(DashboardUtils.dashboard(serviceDefinition, serviceInstanceId));

        return new ResponseEntity<>(serviceInstanceResponse, HttpStatus.OK);
    }
}
