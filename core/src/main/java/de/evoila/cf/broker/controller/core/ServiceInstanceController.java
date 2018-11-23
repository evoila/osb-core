package de.evoila.cf.broker.controller.core;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.model.annotations.ApiVersion;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.impl.DeploymentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.lang.UnsupportedOperationException;

/** @author Johannes Hiemer, Christian Brinker, Marco Di Martino. */
@RestController
@RequestMapping(value = "/v2/service_instances")
public class ServiceInstanceController extends BaseController {

	private final Logger log = LoggerFactory.getLogger(ServiceInstanceController.class);

	public static final String SERVICE_INSTANCE_BASE_PATH = "/core/service_instances";

	private DeploymentServiceImpl deploymentService;

	private EndpointConfiguration endpointConfiguration;

    private CatalogService catalogService;

    private ServiceInstanceRepository serviceInstanceRepository;

    public ServiceInstanceController(DeploymentServiceImpl deploymentService,
                                     EndpointConfiguration endpointConfiguration,
									 CatalogService catalogService,
                                     ServiceInstanceRepository serviceInstanceRepository) {
    	this.deploymentService = deploymentService;
    	this.endpointConfiguration = endpointConfiguration;
    	this.catalogService = catalogService;
    	this.serviceInstanceRepository = serviceInstanceRepository;
	}

	@PutMapping(value = "/{instanceId}")
	@ApiVersion({ApiVersions.API_213, ApiVersions.API_214})
	public ResponseEntity<ServiceInstanceResponse> createServiceInstance(
			@PathVariable("instanceId") String serviceInstanceId,
			@RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
			@Valid @RequestBody ServiceInstanceRequest request) throws ServiceDefinitionDoesNotExistException,
					ServiceInstanceExistsException, ServiceBrokerException, AsyncRequiredException, InvalidParametersException {

		if (acceptsIncomplete == null || !acceptsIncomplete) {
			throw new AsyncRequiredException();
		}

		log.debug("PUT: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
				+ ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId);

		ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());

		if (svc == null) {
			throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
		}

		Extension extension = new Extension(endpointConfiguration.getDefault() + "/custom/v2/extensions", svc.getDashboard().getAuthEndpoint());

		ServiceInstanceResponse response = deploymentService.createServiceInstance(serviceInstanceId, request, extension.getExtensionApis());

		if (DashboardUtils.hasDashboard(svc))
			response.setDashboardUrl(DashboardUtils.dashboard(svc, serviceInstanceId));
		log.debug("ServiceInstance Created: " + serviceInstanceId);


		if (response.isAsync())
			return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
		else
			return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping(value = "/{instanceId}/last_operation")
	@ApiVersion({ApiVersions.API_213, ApiVersions.API_214})
	public ResponseEntity<JobProgressResponse> lastOperation(
			@PathVariable("instanceId") String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException {

		JobProgressResponse serviceInstanceProcessingResponse = deploymentService.getLastOperation(serviceInstanceId);

		return new ResponseEntity<>(serviceInstanceProcessingResponse, HttpStatus.OK);
	}

	@PatchMapping(value= "/{instanceId}")
	@ApiVersion({ApiVersions.API_213, ApiVersions.API_214})
	public ResponseEntity<String> updateServiceInstance(@PathVariable("instanceId") String serviceInstanceId,
				@RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
				@RequestBody ServiceInstanceRequest request) throws ServiceBrokerException, ServiceDefinitionDoesNotExistException,
            ServiceInstanceDoesNotExistException, AsyncRequiredException, InvalidParametersException{

		if (request.getServiceDefinitionId() == null){
			return new ResponseEntity<>("Missing required fields: service_id", HttpStatus.BAD_REQUEST );
		}

		log.debug("PATCH: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
				+ ", updateServiceInstance(), serviceInstanceId = " + serviceInstanceId);

		if (acceptsIncomplete == null || !acceptsIncomplete){
			throw new AsyncRequiredException();
		}

		if (catalogService.getServiceDefinition(request.getServiceDefinitionId()).isUpdateable()){
			deploymentService.updateServiceInstance(serviceInstanceId, request);
		} else {
			return new ResponseEntity<>("{}", HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return new ResponseEntity<>("{}", HttpStatus.ACCEPTED);

	}

	@DeleteMapping(value = "/{instanceId}")
	@ApiVersion({ApiVersions.API_213, ApiVersions.API_214})
	public ResponseEntity<String> deleteServiceInstance(
			@PathVariable("instanceId") String instanceId,
			@RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
			@RequestParam("service_id") String serviceId, @RequestParam("plan_id") String planId) throws ServiceBrokerException, AsyncRequiredException,
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

	@GetMapping(value = "/{instanceId}")
	@ApiVersion(ApiVersions.API_214)
	public ResponseEntity<ServiceInstanceResponse> fetchServiceInstance(
			@RequestHeader("X-Broker-API-Version") String apiHeader,
			@PathVariable("instanceId") String instanceId) throws ServiceInstanceDoesNotExistException, UnsupportedOperationException,
			ServiceBrokerException, ConcurrencyErrorException, ServiceInstanceNotFoundException{


		ServiceInstance serviceInstance = deploymentService.fetchServiceInstance(instanceId);

		if (!(catalogService.getServiceDefinition(serviceInstance.getServiceDefinitionId()).isInstancesRetrievable())){
			throw new ServiceInstanceNotRetrievableException("The Service Instance could not be retrievable. You should not attempt to call this endpoint");
		}
		ServiceInstanceResponse serviceInstanceResponse = new ServiceInstanceResponse(serviceInstance);
		return new ResponseEntity<>(serviceInstanceResponse, HttpStatus.OK);
	}

	@ExceptionHandler({ AsyncRequiredException.class})
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(AsyncRequiredException ex) {
		return processErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);	
	}

	@ExceptionHandler({ ConcurrencyErrorException.class })
	public ResponseEntity<ErrorMessage> handleException(ConcurrencyErrorException ex) {
		return processErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler({ ServiceDefinitionDoesNotExistException.class, InvalidParametersException.class, ServiceInstanceNotRetrievableException.class })
    @ResponseBody
    public ResponseEntity<ErrorMessage> handleException(Exception ex) {
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

	@ExceptionHandler(ServiceInstanceNotFoundException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceNotFoundException ex){
    	return processErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidParametersException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(InvalidParametersException ex) {
		return processErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
}