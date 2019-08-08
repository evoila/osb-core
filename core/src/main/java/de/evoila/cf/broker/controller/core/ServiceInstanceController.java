package de.evoila.cf.broker.controller.core;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.annotations.ApiVersion;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.DeploymentService;
import de.evoila.cf.broker.util.EmptyRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Johannes Hiemer, Christian Brinker, Marco Di Martino.
 **/
@RestController
@RequestMapping(value = "/v2/service_instances")
public class ServiceInstanceController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(ServiceInstanceController.class);

    public static final String SERVICE_INSTANCE_BASE_PATH = "/v2/service_instances";

    private DeploymentService deploymentService;

    private EndpointConfiguration endpointConfiguration;

    private CatalogService catalogService;

    private ServiceInstanceRepository serviceInstanceRepository;

    public ServiceInstanceController(DeploymentService deploymentService,
                                     EndpointConfiguration endpointConfiguration,
                                     CatalogService catalogService,
                                     ServiceInstanceRepository serviceInstanceRepository) {
        this.deploymentService = deploymentService;
        this.endpointConfiguration = endpointConfiguration;
        this.catalogService = catalogService;
        this.serviceInstanceRepository = serviceInstanceRepository;
    }

    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214})
    @PutMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<ServiceInstanceOperationResponse> create(
            @PathVariable("serviceInstanceId") String serviceInstanceId,
            @RequestHeader(value = "X-Broker-API-Originating-Identity") String originatingIdentity,
            @RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
            @Valid @RequestBody ServiceInstanceRequest request) throws ServiceDefinitionDoesNotExistException,
            ServiceInstanceExistsException, ServiceBrokerException, AsyncRequiredException {

        if (acceptsIncomplete == null || !acceptsIncomplete) {
            throw new AsyncRequiredException();
        }

        log.debug("PUT: " + SERVICE_INSTANCE_BASE_PATH + "/{serviceInstanceId}"
                + ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId);

        ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());

        if (svc == null) {
            throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
        }

        ServiceInstanceOperationResponse response = deploymentService.createServiceInstance(serviceInstanceId, request);

        if (DashboardUtils.hasDashboard(svc))
            response.setDashboardUrl(DashboardUtils.dashboard(svc, serviceInstanceId));
        log.debug("ServiceInstance Creation Started: " + serviceInstanceId);

        if (response.isAsync())
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        else
            return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214})
    @PatchMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<ServiceInstanceOperationResponse> update(
            @PathVariable("serviceInstanceId") String serviceInstanceId,
            @RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
            @RequestBody ServiceInstanceUpdateRequest request,
            @RequestHeader(value = "X-Broker-API-Originating-Identity") String originatingIdentity
    ) throws ServiceBrokerException, ServiceDefinitionDoesNotExistException,
            ServiceInstanceDoesNotExistException, AsyncRequiredException {

        if (request.getServiceDefinitionId() == null) {
            return new ResponseEntity("Missing required fields: service_id", HttpStatus.BAD_REQUEST);
        }

        log.debug("PATCH: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
                + ", updateServiceInstance(), serviceInstanceId = " + serviceInstanceId);

        if (acceptsIncomplete == null || !acceptsIncomplete) {
            throw new AsyncRequiredException();
        }

        ServiceInstanceOperationResponse serviceInstanceOperationResponse = new ServiceInstanceOperationResponse();
        if (catalogService.getServiceDefinition(request.getServiceDefinitionId()).isUpdateable()) {
            serviceInstanceOperationResponse = deploymentService.updateServiceInstance(serviceInstanceId, request);
        } else {
            return new ResponseEntity(EmptyRestResponse.BODY, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity(serviceInstanceOperationResponse, HttpStatus.ACCEPTED);
    }

    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214})
    @DeleteMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<ServiceInstanceOperationResponse> delete(
            @PathVariable("serviceInstanceId") String serviceInstanceId,
            @RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
            @RequestParam("service_id") String serviceId, @RequestParam("plan_id") String planId) throws ServiceBrokerException, AsyncRequiredException,
            ServiceDefinitionDoesNotExistException, ServiceInstanceDoesNotExistException {

        log.debug("DELETE: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
                + ", deleteServiceInstanceBinding(), serviceInstanceId = " + serviceInstanceId + ", serviceId = " + serviceId
                + ", planId = " + planId);

        if (acceptsIncomplete == null || !acceptsIncomplete) {
            throw new AsyncRequiredException();
        }

        ServiceInstanceOperationResponse serviceInstanceOperationResponse = deploymentService.deleteServiceInstance(serviceInstanceId);

        log.debug("ServiceInstance Deleted: " + serviceInstanceId);

        return new ResponseEntity<>(serviceInstanceOperationResponse, HttpStatus.ACCEPTED);
    }

    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214})
    @GetMapping(value = "/{serviceInstanceId}/last_operation")
    public ResponseEntity<JobProgressResponse> lastOperation(
            @PathVariable("serviceInstanceId") String serviceInstanceId,
            @RequestParam(value = "operation", required = false) String operation)
            throws ServiceInstanceDoesNotExistException {

        JobProgressResponse jobProgressResponse;
        if (operation != null)
            jobProgressResponse = deploymentService.getLastOperationById(serviceInstanceId, operation);
        else
            jobProgressResponse = deploymentService.getLastOperationByReferenceId(serviceInstanceId);

        return new ResponseEntity<>(jobProgressResponse, HttpStatus.OK);
    }

    @ApiVersion(ApiVersions.API_214)
    @GetMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<ServiceInstanceResponse> get(@PathVariable("serviceInstanceId") String serviceInstanceId) throws UnsupportedOperationException,
            ServiceBrokerException, ConcurrencyErrorException, ServiceInstanceNotFoundException {

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
