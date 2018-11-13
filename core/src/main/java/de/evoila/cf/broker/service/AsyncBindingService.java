package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.service.impl.DeploymentServiceImpl;

import java.util.Map;

/**
 * @author Marco Di Martino
 */

public interface AsyncBindingService {

    ServiceInstanceBindingResponse asyncCreateServiceInstanceBinding(BindingServiceImpl bindingService, String bindingId, ServiceInstance serviceInstance,
                                                                     ServiceInstanceBindingRequest serviceInstanceBindingRequest, Plan plan, boolean async);

    /*void asyncDeleteServiceInstanceBinding(BindingServiceImpl bindingServiceImpl,
                             ServiceInstance serviceInstance, Plan plan, PlatformService platformService)
            throws ServiceInstanceDoesNotExistException;
*/
    JobProgress getProgress(String serviceInstanceId);

}

