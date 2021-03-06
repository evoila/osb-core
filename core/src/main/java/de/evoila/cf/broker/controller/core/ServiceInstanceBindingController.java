package de.evoila.cf.broker.controller.core;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.ErrorMessage;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/** @author Johannes Hiemer. */
@Controller
@RequestMapping(value = "/v2/service_instances")
public class ServiceInstanceBindingController extends BaseController {

	private final Logger log = LoggerFactory.getLogger(ServiceInstanceBindingController.class);

	public static final String SERVICE_INSTANCE_BINDING_BASE_PATH = "/core/service_instances/{instanceId}/service_bindings";

	private BindingServiceImpl bindingService;

	public ServiceInstanceBindingController(BindingServiceImpl bindingService) {
		this.bindingService = bindingService;
	}

	@PutMapping(value = "/{instanceId}/service_bindings/{bindingId}")
	public ResponseEntity<ServiceInstanceBindingResponse> bindServiceInstance(@PathVariable("instanceId") String instanceId,
            @PathVariable("bindingId") String bindingId,
			@Valid @RequestBody ServiceInstanceBindingRequest request)
					throws ServiceInstanceDoesNotExistException, ServiceInstanceBindingExistsException,
					ServiceBrokerException, ServiceDefinitionDoesNotExistException,
					ServiceInstanceBindingBadRequestException, ServiceBrokerFeatureIsNotSupportedException, InvalidParametersException {

		log.debug("PUT: " + SERVICE_INSTANCE_BINDING_BASE_PATH + "/{bindingId}"
				+ ", bindServiceInstance(), instanceId = " + instanceId + ", bindingId = " + bindingId);

		// AppGuid Field is deprecated and won't be maintained in future. According to OSB spec at:
        // https://github.com/openservicebrokerapi/servicebroker/blob/master/spec.md#bind-resource-object
        // AppGuid may not be present and empty
		if (request.getAppGuid() != null && request.getAppGuid().isEmpty())
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);

		ServiceInstanceBindingResponse response = bindingService.createServiceInstanceBinding(bindingId, instanceId, request);

		log.debug("ServiceInstanceBinding Created: " + bindingId);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@DeleteMapping(value = "/{instanceId}/service_bindings/{bindingId}")
	public ResponseEntity<String> deleteServiceInstanceBinding(@PathVariable("instanceId") String instanceId,
			@PathVariable("bindingId") String bindingId, @RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) throws ServiceBrokerException {

		log.debug("DELETE: " + SERVICE_INSTANCE_BINDING_BASE_PATH + "/{bindingId}"
				+ ", deleteServiceInstanceBinding(),  serviceInstance.id = " + instanceId + ", bindingId = " + bindingId
				+ ", serviceId = " + serviceId + ", planId = " + planId);

		try {
			bindingService.deleteServiceInstanceBinding(bindingId, planId);
		} catch (ServiceInstanceBindingDoesNotExistsException | ServiceDefinitionDoesNotExistException e) {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}

		log.debug("ServiceInstanceBinding Deleted: " + bindingId);

		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceDoesNotExistException ex) {
		return processErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ServiceBrokerFeatureIsNotSupportedException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceBrokerFeatureIsNotSupportedException ex) {
		return processErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceInstanceBindingExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceBindingExistsException ex) {
		return processErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(ServiceInstanceBindingBadRequestException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceBindingBadRequestException ex) {
		return processErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

}
