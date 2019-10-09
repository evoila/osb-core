package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;

import java.util.List;
import java.util.Optional;

/**
 * @author Christian Brinker, Marco Di Martinoevoila.
 */
public interface ServiceInstanceRepository {

	/**
	 * Return the service instance of the given instanceId or throws a exception if none was found.
	 * This way no nullpointer exception can occur.
	 *
	 * If null is a valid option use {@linkplain #getServiceInstanceOptional(String)}.
	 *
	 * @param instanceId The Id of the desired ServiceInstance
	 * @return The ServiceInstance
	 * @throws ServiceInstanceDoesNotExistException - if the service instance does not exist.
	 */
	ServiceInstance getServiceInstance(String instanceId) throws ServiceInstanceDoesNotExistException;

	/**
	 * Returns a optional with the service instance matching the instanceId, if it was found.
	 * If not the optional contains null.
	 * Use this Method if null is an acceptable value for the service instance. If not use {@linkplain #getServiceInstance(String)}.
	 *
	 * @param instanceId the instanceId of the desired ServiceInstance.
	 * @return an optional containing the service instance if it was found.
	 */
	Optional<ServiceInstance> getServiceInstanceOptional(String instanceId);

	List<ServiceInstance> getServiceInstancesByServiceDefinitionId(String serviceDefinitionId);

	boolean containsServiceInstanceId(String serviceInstanceId);

	/**
	 * Method to add a new service instance to the repository.
	 * Setting a service instance with an id unequal to the id parameter will save a copy of the instance with the different id.
	 * Deprecated for usage of {@linkplain #saveServiceInstance(ServiceInstance)}
	 * @param id id for the service instance
	 * @param serviceInstance service instance object to add / save
	 */
	@Deprecated
	void addServiceInstance(String id, ServiceInstance serviceInstance);

	void saveServiceInstance(ServiceInstance serviceInstance);

	void deleteServiceInstance(String serviceInstanceId);

	void updateServiceInstance(ServiceInstance serviceInstance);

}