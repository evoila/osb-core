package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.impl.DeploymentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Johannes Hiemer.
 * @author Christian Brinker, evoila.
 * @author Marco Di Martino.
 */
@RestController
@RequestMapping(value = "/v2")
public class ServiceInstanceController extends BaseController {

	private final Logger log = LoggerFactory.getLogger(ServiceInstanceController.class);

	public static final String SERVICE_INSTANCE_BASE_PATH = "/v2/service_instances";

	@Autowired
	private DeploymentServiceImpl deploymentService;

	@Autowired
	private CatalogService catalogService;

	@Autowired
	private EndpointConfiguration endpointConfiguration;

	@PutMapping(value = "/service_instances/{instanceId}")
	public ResponseEntity<ServiceInstanceResponse> createServiceInstance(
			@PathVariable("instanceId") String serviceInstanceId,
			@RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
			@Valid @RequestBody ServiceInstanceRequest request) throws ServiceDefinitionDoesNotExistException,
					ServiceInstanceExistsException, ServiceBrokerException, AsyncRequiredException {

		if (acceptsIncomplete == null || !acceptsIncomplete) {
			throw new AsyncRequiredException();
		}

		log.debug("PUT: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
				+ ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId);

		ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());

		if (svc == null) {
			throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
		}

		Extension extension = new Extension(endpointConfiguration.getDefault() + "/v2/extensions", svc.getDashboard().getAuthEndpoint());

		ServiceInstanceResponse response = deploymentService.createServiceInstance(serviceInstanceId, request, extension.getExtensionApis());

		if (DashboardUtils.hasDashboard(svc))
			response.setDashboardUrl(DashboardUtils.dashboard(svc, serviceInstanceId));
		log.debug("ServiceInstance Created: " + serviceInstanceId);

		if (response.isAsync())
			return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
		else
			return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping(value = "/service_instances/{instanceId}/last_operation")
	public ResponseEntity<JobProgressResponse> lastOperation(@PathVariable("instanceId") String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException {

		JobProgressResponse serviceInstanceProcessingResponse = deploymentService.getLastOperation(serviceInstanceId);

		return new ResponseEntity<>(serviceInstanceProcessingResponse, HttpStatus.OK);
	}

	@PatchMapping(value= "/service_instances/{instanceId}")
	public ResponseEntity<String> updateServiceInstance(@PathVariable("instanceId") String serviceInstanceId,
				@RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
				@RequestBody ServiceInstanceRequest request) throws ServiceBrokerException, ServiceDefinitionDoesNotExistException,
            ServiceInstanceDoesNotExistException, AsyncRequiredException {

		if (request.getServiceDefinitionId() == null){
			return new ResponseEntity<>("Missing required fields: service_id", HttpStatus.BAD_REQUEST );
		}

		log.debug("PATCH: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
				+ ", updateServiceInstance(), serviceInstanceId = " + serviceInstanceId);

		if (acceptsIncomplete==null || !acceptsIncomplete){
			throw new AsyncRequiredException();
		}

		if (catalogService.getServiceDefinition(request.getServiceDefinitionId()).isUpdateable()){
			deploymentService.updateServiceInstance(serviceInstanceId, request);
		} else {
			return new ResponseEntity<>("{}", HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return new ResponseEntity<>("{}", HttpStatus.ACCEPTED);

	}

	@DeleteMapping(value = "/service_instances/{instanceId}")
	public ResponseEntity<String> deleteServiceInstance(@PathVariable("instanceId") String instanceId,
														@RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
														@RequestParam("service_id") String serviceId, @RequestParam("plan_id") String planId)
			throws ServiceBrokerException, AsyncRequiredException,
            ServiceDefinitionDoesNotExistException, ServiceInstanceDoesNotExistException {

		log.debug("DELETE: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
				+ ", deleteServiceInstanceBinding(), serviceInstanceId = " + instanceId + ", serviceId = " + serviceId
				+ ", planId = "+planId);

		if (acceptsIncomplete==null || !acceptsIncomplete){
			throw new AsyncRequiredException();
		}

		deploymentService.deleteServiceInstance(instanceId);

		log.debug("ServiceInstance Deleted: " + instanceId);

	    return new ResponseEntity<>("{}", HttpStatus.ACCEPTED);
	}

	@ExceptionHandler(ParameterNotNullException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ParameterNotNullException ex, HttpServletResponse response){
		return processErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ AsyncRequiredException.class })
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(AsyncRequiredException ex, HttpServletResponse response) {
		return processErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);	
	}

    @ExceptionHandler({ ServiceDefinitionDoesNotExistException.class })
    @ResponseBody
    public ResponseEntity<ErrorMessage> handleException(ServiceDefinitionDoesNotExistException ex, HttpServletResponse response) {
        return processErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

	@ExceptionHandler(ServiceInstanceExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceExistsException ex) {
	return processErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceDoesNotExistException ex) {
		return processErrorResponse("{}", HttpStatus.GONE);
	}
}