package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Christian Brinker, evoila.
 * @author Marco Di Martino, evoila.
 */
public interface ServiceInstanceRepository {

	ServiceInstance getServiceInstance(String instanceId);

	boolean containsServiceInstanceId(String serviceInstanceId);

	void addServiceInstance(String id, ServiceInstance serviceInstance);

	void deleteServiceInstance(String serviceInstanceId);

	void updateServiceInstancePlan(ServiceInstance serviceInstance);

}