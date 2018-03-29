package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
<<<<<<< HEAD
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
=======
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;

/**
 * Handles instances of service definitions.
 *
 * @author Johannes Hiemer.
 */
public interface BindingService {

    /**
<<<<<<< HEAD
     *
     * @param bindingId
     * @param instanceId
     * @param request
     * @param route
     * @return
     * @throws ServiceInstanceBindingExistsException
     * @throws ServiceBrokerException
     * @throws ServiceInstanceDoesNotExistException
     * @throws ServiceDefinitionDoesNotExistException
     */
    ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId, ServiceInstanceBindingRequest request, String route)
          throws ServiceInstanceBindingExistsException, ServiceBrokerException,
            ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException;
=======
     * Create a new binding to a service instance.
     *
     * @param bindingId  The id provided by the cloud controller
     * @param instanceId The id of the service instance
     * @param serviceId  The id of the service
     * @param planId     The plan used for this binding
     * @param appGuid    The guid of the app for the binding
     * @return
     * @throws ServiceInstanceBindingExistsException if the same binding already exists.
     * @throws ServiceBrokerException
     * @throws ServiceInstanceDoesNotExistException
     */

    public ServiceInstanceBindingResponse createServiceInstanceBinding (String bindingId, String instanceId,
                                                                        String serviceId, String planId, boolean generateServiceKey, String route)
          throws ServiceInstanceBindingExistsException, ServiceBrokerException,
                       ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException;
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f

    /**
     * @param id
     * @return The ServiceInstanceBinding or null if one does not exist.
     */
<<<<<<< HEAD
    ServiceInstanceBinding getServiceInstanceBinding(String id);
=======
    ServiceInstanceBinding getServiceInstanceBinding (String id);
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f

    /**
     * Delete the service instance binding. If a binding doesn't exist, return
     * null.
     *
<<<<<<< HEAD
     * @param bindingId, planId
     * @throws ServiceBrokerException
     * @throws ServerviceInstanceBindingDoesNotExistsException
     */
    void deleteServiceInstanceBinding(String bindingId, String planId)
          throws ServiceBrokerException, ServerviceInstanceBindingDoesNotExistsException;
=======
     * @param id
     * @throws ServiceBrokerException
     * @throws ServiceInstanceBindingDoesNotExistsException
     */
    void deleteServiceInstanceBinding (String id)
          throws ServiceBrokerException, ServiceInstanceBindingDoesNotExistsException;
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f

}
