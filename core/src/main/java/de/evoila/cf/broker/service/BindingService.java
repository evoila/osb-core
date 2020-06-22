package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;

/**
 * @author Johannes Hiemer.
 */
public interface BindingService {

    BaseServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId, ServiceInstanceBindingRequest request, boolean async)
            throws ServiceInstanceBindingExistsException, ServiceBrokerException,
            ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, ServiceBrokerFeatureIsNotSupportedException,
            InvalidParametersException, AsyncRequiredException, PlatformException, ServiceDefinitionPlanDoesNotExistException;

    ServiceInstanceBinding fetchServiceInstanceBinding(String bindingId, String instanceId) throws ServiceInstanceBindingNotFoundException;

    BaseServiceInstanceBindingResponse deleteServiceInstanceBinding(String bindingId, String serviceDefinitionId, String planId, boolean async)
          throws ServiceBrokerException, ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceDefinitionPlanDoesNotExistException;

    JobProgressResponse getLastOperationByReferenceId(String bindingId) throws ServiceInstanceBindingDoesNotExistsException;

    JobProgressResponse getLastOperationById(String bindingId, String jobProgressId) throws ServiceInstanceBindingDoesNotExistsException;

}
