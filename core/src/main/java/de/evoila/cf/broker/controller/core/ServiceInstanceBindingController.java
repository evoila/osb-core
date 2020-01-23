package de.evoila.cf.broker.controller.core;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.annotations.ApiVersion;
import de.evoila.cf.broker.model.annotations.ResponseAdvice;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.util.ParameterValidator;
import de.evoila.cf.broker.util.UuidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * @author Marco Di Martino, Johannes Hiemer
 **/
@Controller
@RequestMapping(value = "/v2/service_instances")
public class ServiceInstanceBindingController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(ServiceInstanceBindingController.class);

    private static final String SERVICE_INSTANCE_BINDING_BASE_PATH = "/core/service_instances/{instanceId}/service_bindings";

    private BindingServiceImpl bindingService;

    private CatalogService catalogService;

    public ServiceInstanceBindingController(BindingServiceImpl bindingService, CatalogService catalogService) {
        this.bindingService = bindingService;
        this.catalogService = catalogService;
    }

    @ResponseAdvice
    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214, ApiVersions.API_215})
    @PutMapping(value = "/{instanceId}/service_bindings/{bindingId}")
    public ResponseEntity bindServiceInstance(
            @Pattern(regexp = UuidUtils.UUID_REGEX, message = UuidUtils.NOT_A_UUID_MESSAGE)
            @PathVariable("instanceId") String instanceId,
            @Pattern(regexp = UuidUtils.UUID_REGEX, message = UuidUtils.NOT_A_UUID_MESSAGE)
            @PathVariable("bindingId") String bindingId,
            @RequestHeader("X-Broker-API-Version") String apiHeader,
            @RequestHeader(value = "X-Broker-API-Request-Identity", required = false) String requestIdentity,
            @RequestHeader(value = "X-Broker-API-Originating-Identity", required = false) String originatingIdentity,
            @RequestParam(value = "accepts_incomplete", required = false, defaultValue = "") Boolean acceptsIncomplete,
            @Valid @RequestBody ServiceInstanceBindingRequest request)
            throws ServiceInstanceBindingExistsException,
            ServiceBrokerException, ServiceDefinitionDoesNotExistException,
            InvalidParametersException, AsyncRequiredException, PlatformException, UnsupportedOperationException {

        log.debug("PUT: " + SERVICE_INSTANCE_BINDING_BASE_PATH + "/{bindingId}"
                + ", bindServiceInstance(), instanceId = " + instanceId + ", bindingId = " + bindingId);

        // AppGuid Field is deprecated and won't be maintained in future. According to OSB spec at:
        // https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#bind-resource-object
        // AppGuid may not be present and empty
        if (request.getAppGuid() != null && request.getAppGuid().isEmpty())
            return processEmptyErrorResponse(HttpStatus.BAD_REQUEST);

        if (acceptsIncomplete == null) {
            acceptsIncomplete = false;
        }

        if (acceptsIncomplete && apiHeader.equals("2.13")) {
            throw new ServiceInstanceBindingBadRequestException(bindingId);
        }
        BaseServiceInstanceBindingResponse serviceInstanceBindingResponse;
        try {
            serviceInstanceBindingResponse = bindingService.createServiceInstanceBinding(bindingId,
                    instanceId, request, acceptsIncomplete);
        } catch (ServiceInstanceDoesNotExistException ex) {
            return processErrorResponse(ex.getError(), ex.getMessage(), HttpStatus.NOT_FOUND);
        }

        log.debug("ServiceInstanceBinding Created: " + bindingId);

        if (serviceInstanceBindingResponse.isAsync())
            return new ResponseEntity<>(serviceInstanceBindingResponse, HttpStatus.ACCEPTED);
        else
            return new ResponseEntity<>(serviceInstanceBindingResponse, HttpStatus.CREATED);

    }

    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214, ApiVersions.API_215})
    @DeleteMapping(value = "/{instanceId}/service_bindings/{bindingId}")
    public ResponseEntity unbind(@PathVariable("instanceId") String instanceId,
                                 @PathVariable("bindingId") String bindingId, @RequestParam("service_id") String serviceId,
                                 @RequestParam("plan_id") String planId, @RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
                                 @RequestHeader("X-Broker-API-Version") String apiHeader,
                                 @RequestHeader(value = "X-Broker-API-Request-Identity", required = false) String requestIdentity,
                                 @RequestHeader(value = "X-Broker-API-Originating-Identity", required = false) String originatingIdentity
    ) throws ServiceBrokerException, AsyncRequiredException {

        log.debug("DELETE: " + SERVICE_INSTANCE_BINDING_BASE_PATH + "/{bindingId}"
                + ", deleteServiceInstanceBinding(),  serviceInstance.id = " + instanceId + ", bindingId = " + bindingId
                + ", serviceId = " + serviceId + ", planId = " + planId);

        if (acceptsIncomplete == null) {
            acceptsIncomplete = false;
        }

        if (acceptsIncomplete && apiHeader.equals("2.13")) {
            throw new ServiceInstanceBindingBadRequestException(bindingId);
        }

        BaseServiceInstanceBindingResponse baseServiceInstanceBindingResponse;
        try {
            baseServiceInstanceBindingResponse = bindingService
                    .deleteServiceInstanceBinding(bindingId, planId, acceptsIncomplete);
        } catch (ServiceInstanceBindingDoesNotExistsException | ServiceDefinitionDoesNotExistException e) {
            return processEmptyErrorResponse(HttpStatus.GONE);
        }

        log.debug("ServiceInstanceBinding Deleted: " + bindingId);

        if (baseServiceInstanceBindingResponse == null ||
            !(baseServiceInstanceBindingResponse.isAsync())) {

            return processEmptyErrorResponse(HttpStatus.OK);
        }
        return new ResponseEntity<>(baseServiceInstanceBindingResponse, HttpStatus.ACCEPTED);
    }

    @ApiVersion({ApiVersions.API_214, ApiVersions.API_215})
    @GetMapping(value = "/{instanceId}/service_bindings/{bindingId}/last_operation")
    public ResponseEntity<JobProgressResponse> lastOperation(
            @PathVariable("instanceId") String instanceId,
            @PathVariable("bindingId") String bindingId,
            @RequestParam(value = "service_id", required = false) String serviceId,
            @RequestParam(value = "plan_id", required = false) String planId,
            @RequestHeader(value = "X-Broker-API-Request-Identity", required = false) String requestIdentity,
            @RequestHeader(value = "X-Broker-API-Originating-Identity", required = false) String originatingIdentity,
            @RequestHeader(value = "operation", required = false) String operation)
            throws ServiceInstanceBindingDoesNotExistsException {

        JobProgressResponse jobProgressResponse;
        if (operation != null)
            jobProgressResponse = bindingService.getLastOperationById(bindingId, operation);
        else
            jobProgressResponse = bindingService.getLastOperationByReferenceId(bindingId);

        return new ResponseEntity<>(jobProgressResponse, HttpStatus.OK);
    }

    @ApiVersion({ApiVersions.API_214, ApiVersions.API_215})
    @GetMapping(value = "/{instanceId}/service_bindings/{bindingId}")
    public ResponseEntity fetch(
            @PathVariable("instanceId") String instanceId,
            @PathVariable("bindingId") String bindingId,
            @RequestHeader(value = "X-Broker-API-Originating-Identity", required = false) String originatingIdentity,
            @RequestHeader(value = "X-Broker-API-Request-Identity", required = false) String requestIdentity) throws
            ServiceInstanceBindingNotFoundException, ServiceBrokerException, ServiceDefinitionDoesNotExistException {

        ServiceInstance serviceInstance;
        try {
            serviceInstance = bindingService.getServiceInstance(instanceId);
        } catch (ServiceInstanceDoesNotExistException ex) {
            return processErrorResponse(ex.getError(), ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if (!(catalogService.getServiceDefinition(serviceInstance.getServiceDefinitionId()).isBindingsRetrievable())) {
            throw new ServiceInstanceBindingNotRetrievableException("The Service Binding could not be retrievable. You should not attempt to call this endpoint");
        }

        ServiceInstanceBinding binding = bindingService.fetchServiceInstanceBinding(bindingId, instanceId);
        ServiceInstanceBindingResponse response = new ServiceInstanceBindingResponse(binding);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
