/**
 * 
 */
package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

import java.util.Map;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface PlatformService {

    /**
     *
     */
    void registerCustomPlatformService();

	/**
	 * @param plan
	 * @return
	 */
	boolean isSyncPossibleOnCreate(Plan plan);

	/**
	 * @param serviceInstance
	 * @return
	 */
	boolean isSyncPossibleOnDelete(ServiceInstance serviceInstance);


	boolean isSyncPossibleOnBind();

	boolean isSyncPossibleOnUnbind();

	/**
	 * @param serviceInstance, plan
	 * @return
	 */
	boolean isSyncPossibleOnUpdate(ServiceInstance serviceInstance, Plan plan);

    /**
     *
     * @param serviceInstance
     * @param plan
     * @return
     * @throws ServiceBrokerException
     * @throws PlatformException
     */
    ServiceInstance preCreateInstance(ServiceInstance serviceInstance, Plan plan)
            throws PlatformException;

	/**
	 * @param serviceInstance
	 * @param plan
	 * @return new ServiceInstance with updated fields
	 * @throws Exception 
	 */
	ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan, Map<String, Object> customParameters) throws PlatformException;

    /**
     *
     * @param serviceInstance
     * @param plan
     * @return
     */
    ServiceInstance getCreateInstancePromise(ServiceInstance serviceInstance, Plan plan);

    /**
     *
     * @param serviceInstance
     * @param plan
     * @return
     * @throws ServiceBrokerException
     * @throws PlatformException
     */
    ServiceInstance postCreateInstance(ServiceInstance serviceInstance, Plan plan)
            throws PlatformException;

    /**
     *
     * @param serviceInstance
     */
    void preDeleteInstance(ServiceInstance serviceInstance) throws PlatformException;

	/**
	 * @param serviceInstance
	 * @throws ServiceInstanceDoesNotExistException 
	 * @throws ServiceBrokerException 
	 */
	void deleteInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException;

    /**
     *
     * @param serviceInstance
     * @return
     * @throws ServiceBrokerException
     * @throws PlatformException
     */
    void postDeleteInstance(ServiceInstance serviceInstance) throws PlatformException;

    /**
     *
     * @param serviceInstance
     * @param plan
     * @return
     * @throws ServiceBrokerException
     * @throws PlatformException
     */
    ServiceInstance preUpdateInstance(ServiceInstance serviceInstance, Plan plan)
            throws PlatformException;

	/**
	 * @param serviceInstance
	 * @param plan
	 * @return new ServiceInstance with updated fields
	 */
	ServiceInstance updateInstance(ServiceInstance serviceInstance, Plan plan, Map<String, Object> customParameters) throws PlatformException;

    /**
     *
     * @param serviceInstance
     * @param plan
     * @return
     * @throws ServiceBrokerException
     * @throws PlatformException
     */
    ServiceInstance postUpdateInstance(ServiceInstance serviceInstance, Plan plan)
            throws PlatformException;

}
