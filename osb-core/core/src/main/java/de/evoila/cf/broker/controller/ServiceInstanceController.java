package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.DeploymentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.lang.reflect.Method;
import java.rmi.activation.ActivationSystem;

/**
 * @author Johannes Hiemer.
 * @author Christian Brinker, evoila.
 * @author Marco Di Martino.
 */
@Controller
@RequestMapping(value = "/v2")
public class ServiceInstanceController extends BaseController {

	private final Logger log = LoggerFactory.getLogger(ServiceInstanceController.class);

	public static final String SERVICE_INSTANCE_BASE_PATH = "/v2/service_instances";

	@Autowired
	private DeploymentServiceImpl deploymentService;

	@Autowired
	private CatalogService catalogService;

	public ServiceInstanceController() {
	}

	@RequestMapping(value = "/service_instances/{instanceId}", method = RequestMethod.PUT)
	public ResponseEntity<ServiceInstanceResponse> createServiceInstance(


			@PathVariable("instanceId") String serviceInstanceId,
			@RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
			@Valid @RequestBody ServiceInstanceRequest request) throws ServiceDefinitionDoesNotExistException,
			ServiceInstanceExistsException, ServiceBrokerException, AsyncRequiredException, ParameterNotNullException {


		if (acceptsIncomplete == null || !acceptsIncomplete) {
			throw new AsyncRequiredException();
		}

		// currently not dealing with parameters
		if (request.getParameters() != null && request.getParameters().size() > 0){
			throw new ParameterNotNullException(request.getParameters());
		}


		log.debug("PUT: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
				+ ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId);

		ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());

		if (svc == null) {
			throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
		}

		ServiceInstanceResponse response = deploymentService.createServiceInstance(serviceInstanceId,
				request.getServiceDefinitionId(), request.getPlanId(), request.getOrganizationGuid(),
				request.getSpaceGuid(), request.getParameters(), request.getContext());


		if (DashboardUtils.hasDashboard(svc))
			response.setDashboardUrl(DashboardUtils.dashboard(svc, serviceInstanceId));
		log.debug("ServiceInstance Created: " + serviceInstanceId);

		if (response.isAsync())
			return new ResponseEntity<ServiceInstanceResponse>(response, HttpStatus.ACCEPTED);
		else
			return new ResponseEntity<ServiceInstanceResponse>(response, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/service_instances/{instanceId}/last_operation", method = RequestMethod.GET)
	public ResponseEntity<JobProgressResponse> lastOperation(@PathVariable("instanceId") String serviceInstanceId)
			throws ServiceDefinitionDoesNotExistException, ServiceInstanceExistsException, ServiceBrokerException,
			ServiceInstanceDoesNotExistException {

		JobProgressResponse serviceInstanceProcessingResponse = deploymentService.getLastOperation(serviceInstanceId);

		return new ResponseEntity<JobProgressResponse>(serviceInstanceProcessingResponse, HttpStatus.OK);
	}


	@RequestMapping(value= "/service_instances/{instanceId}", method = RequestMethod.PATCH)
	public ResponseEntity<String> updateServiceInstance(

				@PathVariable("instanceId") String serviceInstanceId,
				@RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
				@RequestBody ServiceInstanceRequest request) throws ServiceBrokerException, ServiceInstanceDoesNotExistException,
				ParameterNotNullException, AsyncRequiredException, ServiceDefinitionDoesNotExistException {
		if (request.getServiceDefinitionId() == null){
			return new ResponseEntity<String>("Missing required fields: service_id", HttpStatus.BAD_REQUEST );
		}

		log.debug("PATCH: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
				+ ", updateServiceInstance(), serviceInstanceId = " + serviceInstanceId);

		if (acceptsIncomplete==null || !acceptsIncomplete){
			throw new AsyncRequiredException();
		}

		if (request.getParameters() != null && request.getParameters().size() > 0) {
			throw new ParameterNotNullException(request.getParameters());
		}

		if (catalogService.getServiceDefinition(request.getServiceDefinitionId()).isUpdateable()){
			deploymentService.updateServiceInstance(serviceInstanceId, request.getPlanId());
		}else{
			return new ResponseEntity<String>("{}", HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return new ResponseEntity<String>("{}", HttpStatus.ACCEPTED);

	}

	@RequestMapping(value = "/service_instances/{instanceId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstance(@PathVariable("instanceId") String instanceId,
														@RequestParam(value = "accepts_incomplete", required = false) Boolean acceptsIncomplete,
														@RequestParam("service_id") String serviceId, @RequestParam("plan_id") String planId)
			throws 	ServiceBrokerException, AsyncRequiredException, ServiceInstanceDoesNotExistException {

		log.debug("DELETE: " + SERVICE_INSTANCE_BASE_PATH + "/{instanceId}"
				+ ", deleteServiceInstanceBinding(), serviceInstanceId = " + instanceId + ", serviceId = " + serviceId
				+ ", planId = "+planId);

		if (acceptsIncomplete==null || !acceptsIncomplete){
			throw new AsyncRequiredException();
		}

		deploymentService.deleteServiceInstance(instanceId);

		log.debug("ServiceInstance Deleted: " + instanceId);

		return new ResponseEntity<String>("{}", HttpStatus.ACCEPTED); //was OK
	}


	@ExceptionHandler(ParameterNotNullException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ParameterNotNullException ex,
														HttpServletResponse response){
		log.warn("Handle exception for [bad parameters]"+ex.getMessage());
		return processErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler({ServiceDefinitionDoesNotExistException.class, AsyncRequiredException.class})
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(Exception ex, HttpServletResponse response) {
		log.warn("Handle exception for [accepts_incomplete|serviceDefinitionNotExist]"+ex.getMessage());
		return processErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(ServiceInstanceExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceExistsException ex,
														HttpServletResponse response) {
		log.warn("Handle exception for [serviceInstanceExists]"+ex.getMessage());
		return processErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(ServiceInstanceDoesNotExistException ex,
														HttpServletResponse response) {
		log.warn("Handle exception for [serviceInstanceDoesNotExist]"+ex.getMessage());
		return processErrorResponse("{}", HttpStatus.GONE);
	}

}