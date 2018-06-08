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
    ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId, ServiceInstanceBindingRequest request)
          throws ServiceInstanceBindingExistsException, ServiceBrokerException,
            ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException,
            ServiceInstanceBindingBadRequestException, ServiceBrokerFeatureIsNotSupportedException;

    /**
     *
     * @param id
     * @return
     */
    ServiceInstanceBinding getServiceInstanceBinding(String id);

    /**
     *
     * @param bindingId
     * @param planId
     * @throws ServiceBrokerException
     * @throws ServiceInstanceBindingDoesNotExistsException
     * @throws ServiceDefinitionDoesNotExistException
     */
    void deleteServiceInstanceBinding(String bindingId, String planId)
          throws ServiceBrokerException, ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException;
}
