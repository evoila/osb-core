package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;

/**
 * Handles instances of service definitions.
 *
 * @author Johannes Hiemer.
 */
public interface BindingService {

    /**
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

    /**
     * @param id
     * @return The ServiceInstanceBinding or null if one does not exist.
     */
    ServiceInstanceBinding getServiceInstanceBinding(String id);

    /**
     * Delete the service instance binding. If a binding doesn't exist, return
     * null.
     *
     * @param bindingId, planId
     * @throws ServiceBrokerException
     * @throws ServiceInstanceBindingDoesNotExistsException
     */
    void deleteServiceInstanceBinding(String bindingId, String planId)
          throws ServiceBrokerException, ServiceInstanceBindingDoesNotExistsException;
}
