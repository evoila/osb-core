package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;

/**
 * Handles instances of service definitions.
 *
 * @author Johannes Hiemer.
 */
public interface BindingService {


    public ServiceInstanceBindingResponse createServiceInstanceBinding (String bindingId, String instanceId,
                                                                        String serviceId, String planId, boolean generateServiceKey, String route)
          throws ServiceInstanceBindingExistsException, ServiceBrokerException,
                       ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException;

    ServiceInstanceBinding getServiceInstanceBinding (String id);
    
    void deleteServiceInstanceBinding (String id)
          throws ServiceBrokerException, ServerviceInstanceBindingDoesNotExistsException;

}
