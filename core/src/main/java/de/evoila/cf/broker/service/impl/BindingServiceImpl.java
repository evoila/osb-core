/**
 *
 */
package de.evoila.cf.broker.service.impl;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.RouteBinding;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.RouteBindingRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BindingService;
import de.evoila.cf.broker.service.HAProxyService;
import de.evoila.cf.broker.util.ParameterValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 *
 */
public abstract class BindingServiceImpl implements BindingService {

	private final Logger log = LoggerFactory.getLogger(BindingServiceImpl.class);

	protected BindingRepository bindingRepository;

	protected ServiceDefinitionRepository serviceDefinitionRepository;

	protected ServiceInstanceRepository serviceInstanceRepository;

	protected RouteBindingRepository routeBindingRepository;

	protected HAProxyService haProxyService;

	public BindingServiceImpl(BindingRepository bindingRepository, ServiceDefinitionRepository serviceDefinitionRepository,
							  ServiceInstanceRepository serviceInstanceRepository, RouteBindingRepository routeBindingRepository,
							  HAProxyService haProxyService) {
		this.bindingRepository = bindingRepository;
		this.serviceDefinitionRepository = serviceDefinitionRepository;
		this.serviceInstanceRepository = serviceInstanceRepository;
		this.routeBindingRepository = routeBindingRepository;
		this.haProxyService = haProxyService;
	}

	@Override
	public ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			ServiceInstanceBindingRequest serviceInstanceBindingRequest) throws ServiceInstanceBindingExistsException,
			ServiceBrokerException, ServiceDefinitionDoesNotExistException, ServiceInstanceDoesNotExistException, InvalidParametersException {

		validateBindingNotExists(bindingId, instanceId);

		ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);
		if (serviceInstance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		Plan plan = serviceDefinitionRepository.getPlan(serviceInstanceBindingRequest.getPlanId());
		if (serviceInstanceBindingRequest.getParameters() != null && serviceInstanceBindingRequest.getParameters().size() > 0){
			try {
				ParameterValidator.validateParameters(serviceInstanceBindingRequest, plan);
			}catch(ProcessingException e) {
			throw new InvalidParametersException("Error while validating parameters");
			}
		}

		if (serviceInstanceBindingRequest.getBindResource() != null && !StringUtils
                .isEmpty(serviceInstanceBindingRequest.getBindResource().getRoute())) {

			RouteBinding routeBinding = bindRoute(serviceInstance, serviceInstanceBindingRequest.getBindResource().getRoute());
			routeBindingRepository.addRouteBinding(routeBinding);
			ServiceInstanceBindingResponse response = new ServiceInstanceBindingResponse(routeBinding.getRoute());
			return response;
		}

		ServiceInstanceBinding binding;
		if (haProxyService != null && (serviceInstanceBindingRequest.getAppGuid() == null ||
                (serviceInstanceBindingRequest.getBindResource() != null && serviceInstanceBindingRequest.getBindResource().getAppGuid() == null))) {
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
                                                    ServiceInstance serviceInstance, Plan plan, List<ServerAddress> externalAddresses) throws ServiceBrokerException, InvalidParametersException {
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, externalAddresses.get(0));

		ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(),
				credentials);
		serviceInstanceBinding.setExternalServerAddresses(externalAddresses);
		return serviceInstanceBinding;
	}

	protected ServiceInstanceBinding bindService(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                 ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException, InvalidParametersException {
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