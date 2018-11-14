
package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.JobProgressResponse;
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
     * @return
     * @throws ServiceInstanceBindingExistsException
     * @throws ServiceBrokerException
     * @throws ServiceInstanceDoesNotExistException
     * @throws ServiceDefinitionDoesNotExistException
     */
    ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId, ServiceInstanceBindingRequest request, boolean async)
          throws ServiceInstanceBindingExistsException, ServiceBrokerException,
            ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException,
            ServiceInstanceBindingBadRequestException, ServiceBrokerFeatureIsNotSupportedException,
            InvalidParametersException, AsyncRequiredException;

    /**
     *
     * @param bindingId
     * @param instanceId
     * @return
     */
    ServiceInstanceBinding fetchServiceInstanceBinding(String bindingId, String instanceId) throws ServiceInstanceBindingNotFoundException;

    /**
     *
     * @param bindingId
     * @param planId
     * @throws ServiceBrokerException
     * @throws ServiceInstanceBindingDoesNotExistsException
     * @throws ServiceDefinitionDoesNotExistException
     */
    void deleteServiceInstanceBinding(String bindingId, String planId, boolean async)
          throws ServiceBrokerException, ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceInstanceBindingBadRequestException;


    JobProgressResponse getLastOperation(String bindingId) throws ServiceInstanceBindingDoesNotExistsException;

}