/**
<<<<<<< HEAD
 *
=======
 * 
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
 */
package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.RouteBindingRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BindingService;
import de.evoila.cf.broker.service.HAProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 *
 */
public abstract class BindingServiceImpl implements BindingService {

<<<<<<< HEAD
=======

>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
	private final Logger log = LoggerFactory.getLogger(BindingServiceImpl.class);

	@Autowired
	protected BindingRepository bindingRepository;

	@Autowired
	protected ServiceDefinitionRepository serviceDefinitionRepository;

	@Autowired
	protected ServiceInstanceRepository serviceInstanceRepository;

	@Autowired
	protected RouteBindingRepository routeBindingRepository;

<<<<<<< HEAD
	@Autowired(required = false)
	protected HAProxyService haProxyService;

	protected abstract void deleteBinding(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan)
=======
	@Autowired
	protected HAProxyService haProxyService;

	public abstract void deleteBinding(String bindingId, ServiceInstance serviceInstance)
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
			throws ServiceBrokerException;

	@Override
	public ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
<<<<<<< HEAD
            ServiceInstanceBindingRequest serviceInstanceBindingRequest, String route)
			throws ServiceInstanceBindingExistsException, ServiceBrokerException,
			ServiceInstanceDoesNotExistException {

	    String planId = serviceInstanceBindingRequest.getPlanId();

=======
			String serviceId, String planId, boolean generateServiceKey, String route)
					throws ServiceInstanceBindingExistsException, ServiceBrokerException,
					ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
		validateBindingNotExists(bindingId, instanceId);

		ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);

		if (serviceInstance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		Plan plan = serviceDefinitionRepository.getPlan(planId);

<<<<<<< HEAD
=======

>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
		if (route != null) {
			RouteBinding routeBinding = bindRoute(serviceInstance, route);
			routeBindingRepository.addRouteBinding(routeBinding);
			ServiceInstanceBindingResponse response = new ServiceInstanceBindingResponse(routeBinding.getRoute());
			return response;
		}

		ServiceInstanceBinding binding;
<<<<<<< HEAD
		if (serviceInstanceBindingRequest.getAppGuid() == null && haProxyService != null) {
			List<ServerAddress> externalServerAddresses = haProxyService.appendAgent(serviceInstance.getHosts(), bindingId, instanceId);

			binding = bindServiceKey(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, externalServerAddresses);
		} else {
			binding = bindService(bindingId, serviceInstanceBindingRequest, serviceInstance, plan);
		}

		ServiceInstanceBindingResponse response = new ServiceInstanceBindingResponse(binding);

=======
		if (generateServiceKey) {
			List<ServerAddress> externalServerAddresses = haProxyService.appendAgent(serviceInstance.getHosts(), bindingId, instanceId);

			binding = bindServiceKey(bindingId, serviceInstance, plan, externalServerAddresses);
		} else {
			binding = bindService(bindingId, serviceInstance, plan);
		}

		ServiceInstanceBindingResponse response = new ServiceInstanceBindingResponse(binding);
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
		bindingRepository.addInternalBinding(binding);

		return response;
	}

<<<<<<< HEAD
	protected abstract RouteBinding bindRoute(ServiceInstance serviceInstance, String route);

	protected ServiceInstanceBinding createServiceInstanceBinding(String bindingId, String serviceInstanceId,
																  Map<String, Object> credentials, String syslogDrainUrl, String appGuid) {
=======
	/**
	 * @param serviceInstance
	 * @param route
	 * @return
	 */
	protected abstract RouteBinding bindRoute(ServiceInstance serviceInstance, String route);

	protected ServiceInstanceBinding createServiceInstanceBinding(String bindingId, String serviceInstanceId,
			Map<String, Object> credentials, String syslogDrainUrl, String appGuid) {
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId, serviceInstanceId, credentials,
				syslogDrainUrl);
		return binding;
	}

	@Override
<<<<<<< HEAD
	public void deleteServiceInstanceBinding(String bindingId, String planId)
			throws ServerviceInstanceBindingDoesNotExistsException {
=======
	public void deleteServiceInstanceBinding(String bindingId)
			throws ServiceBrokerException, ServiceInstanceBindingDoesNotExistsException {
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
		ServiceInstance serviceInstance = getBinding(bindingId);

		try {
			ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
			List<ServerAddress> externalServerAddresses = binding.getExternalServerAddresses();
<<<<<<< HEAD
			if (externalServerAddresses != null && haProxyService != null) {
				haProxyService.removeAgent(serviceInstance.getHosts(), bindingId);
			}

            Plan plan = serviceDefinitionRepository.getPlan(planId);

			deleteBinding(binding, serviceInstance, plan);
=======
			if (externalServerAddresses != null) {
				haProxyService.removeAgent(serviceInstance.getHosts(), bindingId);
			}

			deleteBinding(bindingId, serviceInstance);
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
		} catch (ServiceBrokerException e) {
			log.error("Could not cleanup service binding", e);
		} finally {
			bindingRepository.deleteBinding(bindingId);
		}
	}

<<<<<<< HEAD
	protected void validateBindingNotExists(String bindingId, String instanceId)
=======
	private void validateBindingNotExists(String bindingId, String instanceId)
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
			throws ServiceInstanceBindingExistsException {
		if (bindingRepository.containsInternalBindingId(bindingId)) {
			throw new ServiceInstanceBindingExistsException(bindingId, instanceId);
		}
	}

<<<<<<< HEAD
	protected ServiceInstance getBinding(String bindingId) throws ServerviceInstanceBindingDoesNotExistsException {
		if (!bindingRepository.containsInternalBindingId(bindingId)) {
			throw new ServerviceInstanceBindingDoesNotExistsException(bindingId);
		}
		String serviceInstanceId = bindingRepository.getInternalBindingId(bindingId);
		if (serviceInstanceId == null) {
			throw new ServerviceInstanceBindingDoesNotExistsException(bindingId);
=======
	private ServiceInstance getBinding(String bindingId) throws ServiceInstanceBindingDoesNotExistsException {
		if (!bindingRepository.containsInternalBindingId(bindingId)) {
			throw new ServiceInstanceBindingDoesNotExistsException(bindingId);
		}
		String serviceInstanceId = bindingRepository.getInternalBindingId(bindingId);
		if (serviceInstanceId == null) {
			throw new ServiceInstanceBindingDoesNotExistsException(bindingId);
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
		}
		return serviceInstanceRepository.getServiceInstance(serviceInstanceId);
	}

<<<<<<< HEAD
	protected ServiceInstanceBinding bindServiceKey(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                    ServiceInstance serviceInstance, Plan plan, List<ServerAddress> externalAddresses) throws ServiceBrokerException {
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, externalAddresses.get(0));

		ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(),
				credentials, null);
=======
	/**
	 * @param bindingId
	 * @param serviceInstance
	 * @param plan
	 * @param externalAddresses
	 * @return
	 * @throws ServiceBrokerException
	 */
	protected ServiceInstanceBinding bindServiceKey(String bindingId, ServiceInstance serviceInstance, Plan plan,
			List<ServerAddress> externalAddresses) throws ServiceBrokerException {
		log.debug("bind service key");

		Map<String, Object> credentials = createCredentials(bindingId, serviceInstance, externalAddresses.get(0));

		ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(),
				credentials, "");
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
		serviceInstanceBinding.setExternalServerAddresses(externalAddresses);
		return serviceInstanceBinding;
	}

<<<<<<< HEAD
	protected ServiceInstanceBinding bindService(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                 ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException {
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, null);

		return new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials, null);
	}

	protected abstract Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                             ServiceInstance serviceInstance, Plan plan, ServerAddress serverAddress) throws ServiceBrokerException;
=======
	/**
	 * @param bindingId
	 * @param serviceInstance
	 * @param plan
	 * @return
	 * @throws ServiceBrokerException
	 */
	protected ServiceInstanceBinding bindService(String bindingId, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {

		log.debug("bind service");
		ServerAddress host = serviceInstance.getHosts().get(0);
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstance, host);
		return new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials, "");
	}

	/**
	 * @param bindingId
	 * @param serviceInstance
	 * @param host
	 * @return
	 * @throws ServiceBrokerException
	 */
	protected abstract Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
			ServerAddress host) throws ServiceBrokerException;
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f

}