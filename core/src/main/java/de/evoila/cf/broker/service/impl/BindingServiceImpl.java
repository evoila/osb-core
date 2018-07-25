/**
 *
 */
package de.evoila.cf.broker.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.RouteBindingRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BindingService;
import de.evoila.cf.broker.service.HAProxyService;
import de.evoila.cf.broker.util.ParameterValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 *
 */
public abstract class BindingServiceImpl implements BindingService {

	private final Logger log = LoggerFactory.getLogger(BindingServiceImpl.class);

	@Autowired
	protected BindingRepository bindingRepository;

	@Autowired
	protected ServiceDefinitionRepository serviceDefinitionRepository;

	@Autowired
	protected ServiceInstanceRepository serviceInstanceRepository;

	@Autowired
	protected RouteBindingRepository routeBindingRepository;

	@Autowired(required = false)
	protected HAProxyService haProxyService;

	@Override
	public ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			ServiceInstanceBindingRequest serviceInstanceBindingRequest) throws ServiceInstanceBindingExistsException,
			ServiceBrokerException, ServiceDefinitionDoesNotExistException, ServiceInstanceDoesNotExistException,
			ServiceInstanceBindingBadRequestException, ServiceBrokerFeatureIsNotSupportedException, InvalidParametersException {

		validateBindingNotExists(bindingId, instanceId);

		ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);
		if (serviceInstance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		Plan plan = serviceDefinitionRepository.getPlan(serviceInstanceBindingRequest.getPlanId());
		try {
			validateParameters(serviceInstanceBindingRequest, plan);
		}catch(ProcessingException e) {
			throw new InvalidParametersException("Error while validating parameters");
		}
		if (serviceInstanceBindingRequest.getBindResource() != null && !StringUtils
                .isEmpty(serviceInstanceBindingRequest.getBindResource().getRoute())) {

			RouteBinding routeBinding = bindRoute(serviceInstance, serviceInstanceBindingRequest.getBindResource().getRoute());
			routeBindingRepository.addRouteBinding(routeBinding);
			ServiceInstanceBindingResponse response = new ServiceInstanceBindingResponse(routeBinding.getRoute());
			return response;
		}

		ServiceInstanceBinding binding;
		if (haProxyService != null && serviceInstanceBindingRequest.getAppGuid() == null && serviceInstanceBindingRequest.getBindResource().getAppGuid() == null) {
			List<ServerAddress> externalServerAddresses = haProxyService.appendAgent(serviceInstance.getHosts(), bindingId, instanceId);

			binding = bindServiceKey(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, externalServerAddresses);
		} else {
			binding = bindService(bindingId, serviceInstanceBindingRequest, serviceInstance, plan);
		}

		bindingRepository.addInternalBinding(binding);

		return new ServiceInstanceBindingResponse(binding);
	}

	protected abstract RouteBinding bindRoute(ServiceInstance serviceInstance, String route);

	protected ServiceInstanceBinding createServiceInstanceBinding(String bindingId, String serviceInstanceId,
																  Map<String, Object> credentials, String syslogDrainUrl, String appGuid) {
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId, serviceInstanceId, credentials,
				syslogDrainUrl);
		return binding;
	}

	@Override
	public void deleteServiceInstanceBinding(String bindingId, String planId)
			throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException {
		ServiceInstance serviceInstance = getBinding(bindingId);

		try {
			ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
			List<ServerAddress> externalServerAddresses = binding.getExternalServerAddresses();
			if (externalServerAddresses != null && haProxyService != null) {
				haProxyService.removeAgent(serviceInstance.getHosts(), bindingId);
			}

            Plan plan = serviceDefinitionRepository.getPlan(planId);

			unbindService(binding, serviceInstance, plan);
		} catch (ServiceBrokerException e) {
			log.error("Could not cleanup service binding", e);
		} finally {
			bindingRepository.unbindService(bindingId);
		}
	}

	protected void validateParameters(ServiceInstanceBindingRequest serviceInstanceBindingRequest, Plan plan) throws ProcessingException, InvalidParametersException {

		/* key validation*/
		HashMap<String, Object> serviceInstanceRequestParams = (HashMap<String, Object>)serviceInstanceBindingRequest.getParameters();
		if (serviceInstanceRequestParams == null){
			return;
		}
		HashMap<String, SchemaProperty> params = null;
		try{
			params = (HashMap<String, SchemaProperty>)plan.getSchemas().getServiceBinding().getCreate().getParameters().getProperties();
		}catch (NullPointerException e){
			throw new InvalidParametersException("No additional parameters are allowed for this request with this plan");
		}

		boolean flag;
		for (String requestKey : serviceInstanceRequestParams.keySet()) {
			flag = false;
			Iterator<Map.Entry<String, SchemaProperty>> entries = params.entrySet().iterator();
			while(!(flag) && entries.hasNext()){
				Map.Entry<String, SchemaProperty> key = entries.next();
				if(requestKey.equals(key.getKey())){
					flag = true;
				}
			}
			if(!(flag)){
				throw new InvalidParametersException(serviceInstanceRequestParams);
			}
		}

		/* schema validation */
		SchemaParameters json = plan.getSchemas().getServiceBinding().getCreate().getParameters();
		HashMap<String, Object> params2;
		params2 = (HashMap<String, Object>)serviceInstanceBindingRequest.getParameters();

		JsonSchema jsonSchema = null;
		JsonNode jsonObject = null;
		try {
			jsonSchema = ParameterValidator.getJsonSchema(json);
			jsonObject = ParameterValidator.getJsonNode(params2);
		}catch (JsonProcessingException e){
			throw new InvalidParametersException("Error while processing json schema");
		}

		log.info("parameters:  --> "+jsonObject.toString());
		try {
			ParameterValidator.validateJson(jsonSchema, jsonObject);
		}catch (ProcessingException e){
			throw new InvalidParametersException("Error while processing json schema");
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
		return serviceInstanceRepository.getServiceInstance(serviceInstanceId);
	}

	protected ServiceInstanceBinding bindServiceKey(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                    ServiceInstance serviceInstance, Plan plan, List<ServerAddress> externalAddresses) throws ServiceBrokerException, ServiceBrokerFeatureIsNotSupportedException, InvalidParametersException {
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, externalAddresses.get(0));

		ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(),
				credentials);
		serviceInstanceBinding.setExternalServerAddresses(externalAddresses);
		return serviceInstanceBinding;
	}

	protected ServiceInstanceBinding bindService(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                 ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException, ServiceInstanceBindingBadRequestException, InvalidParametersException {
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, null);

		return new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials);
	}

    protected abstract void unbindService(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan)
            throws ServiceBrokerException;

	protected abstract Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                             ServiceInstance serviceInstance, Plan plan, ServerAddress serverAddress) throws ServiceBrokerException, InvalidParametersException;

    protected void validateBindingNotExists(String bindingId, String instanceId)
            throws ServiceInstanceBindingExistsException {
        if (bindingRepository.containsInternalBindingId(bindingId)) {
            throw new ServiceInstanceBindingExistsException(bindingId, instanceId);
        }
    }
}