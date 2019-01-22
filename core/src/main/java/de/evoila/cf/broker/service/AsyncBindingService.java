package de.evoila.cf.broker.service;

import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;

/**
 * @author Marco Di Martino.
 **/
public interface AsyncBindingService extends AsyncOperationService {

    void asyncCreateServiceInstanceBinding(BindingServiceImpl bindingService,
                String bindingId, ServiceInstance serviceInstance, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                Plan plan, boolean async, String jobProgressId);

    void asyncDeleteServiceInstanceBinding(BindingServiceImpl bindingServiceImpl, String bindingId,
                             ServiceInstance serviceInstance, Plan plan, String jobProgressId);

}

