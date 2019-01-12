/**
 * 
 */
package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;

import java.util.Map;

/**
 * @author Christian Brinker, Johannes Hiemer.
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
     * @param serviceInstance
     * @param plan
     * @return
     */
	boolean isSyncPossibleOnUpdate(ServiceInstance serviceInstance, Plan plan);

    /**
     * @param serviceInstance
     * @param plan
     * @return
     * @throws PlatformException
     */
    ServiceInstance preCreateInstance(ServiceInstance serviceInstance, Plan plan)
            throws PlatformException;

    /**
     * @param serviceInstance
     * @param plan
     * @param customParameters
     * @return
     * @throws PlatformException
     */
	ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan, Map<String, Object> customParameters) throws PlatformException;

    /**
     * @param serviceInstance
     * @param plan
     * @return
     */
    ServiceInstance getCreateInstancePromise(ServiceInstance serviceInstance, Plan plan);

    /**
     * @param serviceInstance
     * @param plan
     * @return
     * @throws PlatformException
     */
    ServiceInstance postCreateInstance(ServiceInstance serviceInstance, Plan plan)
            throws PlatformException;

    /**
     * @param serviceInstance
     * @throws PlatformException
     */
    void preDeleteInstance(ServiceInstance serviceInstance) throws PlatformException;

    /**
     * @param serviceInstance
     * @param plan
     * @throws PlatformException
     */
	void deleteInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException;

    /**
     * @param serviceInstance
     * @return
     * @throws ServiceBrokerException
     * @throws PlatformException
     */
    void postDeleteInstance(ServiceInstance serviceInstance) throws PlatformException;

    /**
     * @param serviceInstance
     * @param plan
     * @return
     * @throws PlatformException
     */
    ServiceInstance preUpdateInstance(ServiceInstance serviceInstance, Plan plan)
            throws PlatformException;

    /**
     * @param serviceInstance
     * @param plan
     * @param customParameters
     * @return
     * @throws PlatformException
     */
	ServiceInstance updateInstance(ServiceInstance serviceInstance, Plan plan, Map<String, Object> customParameters) throws PlatformException;

    /**
     * @param serviceInstance
     * @param plan
     * @return
     * @throws PlatformException
     */
    ServiceInstance postUpdateInstance(ServiceInstance serviceInstance, Plan plan)
            throws PlatformException;

    /**
     * @param serviceInstance
     * @return
     * @throws PlatformException
     */
    ServiceInstance getInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException;

}
