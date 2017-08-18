/**
 * 
 */
package de.evoila.cf.broker.service;

import java.util.Map;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Christian Brinker, evoila.
 *
 */
public abstract interface PlatformService {

	public void registerCustomPlatformServie();

	public boolean isSyncPossibleOnCreate(Plan plan);


	public boolean isSyncPossibleOnDelete(ServiceInstance instance);

	public boolean isSyncPossibleOnUpdate(ServiceInstance instance, Plan plan);

	public ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan)
			throws PlatformException;

	public void preDeprovisionServiceInstance(ServiceInstance serviceInstance);

	public ServiceInstance createInstance(ServiceInstance instance, Plan plan, Map<String, String> customParameters) throws PlatformException;

	public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan);

	public void deleteServiceInstance(ServiceInstance serviceInstance) throws PlatformException;

	public ServiceInstance updateInstance(ServiceInstance instance, Plan plan);

}
