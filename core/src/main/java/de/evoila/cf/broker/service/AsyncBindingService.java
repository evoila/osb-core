package de.evoila.cf.broker.service;

import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;

/** @author Marco Di Martino */
public interface AsyncBindingService {

    ServiceInstanceBindingResponse asyncCreateServiceInstanceBinding(BindingServiceImpl bindingService, String bindingId, ServiceInstance serviceInstance,
                                                                     ServiceInstanceBindingRequest serviceInstanceBindingRequest, Plan plan, boolean async);

    void asyncDeleteServiceInstanceBinding(BindingServiceImpl bindingServiceImpl, String bindingId,
                             ServiceInstance serviceInstance, Plan plan);

    JobProgress getProgress(String serviceInstanceId);

}

