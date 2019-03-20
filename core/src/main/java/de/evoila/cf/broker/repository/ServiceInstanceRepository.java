package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.ServiceInstance;

import java.util.List;

/**
 * @author Christian Brinker, Marco Di Martinoevoila.
 */
public interface ServiceInstanceRepository {

	ServiceInstance getServiceInstance(String instanceId);

	List<ServiceInstance> getServiceInstancesByServiceDefinitionId(String serviceDefinitionId);

	boolean containsServiceInstanceId(String serviceInstanceId);

	void addServiceInstance(String id, ServiceInstance serviceInstance);

	void deleteServiceInstance(String serviceInstanceId);

	void updateServiceInstance(ServiceInstance serviceInstance);

}