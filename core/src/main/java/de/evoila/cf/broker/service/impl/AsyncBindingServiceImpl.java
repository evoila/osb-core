package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.JobProgressService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Marco Di Martino, Johannes Hiemer.
 **/
@Service
public class AsyncBindingServiceImpl extends AsyncOperationServiceImpl implements AsyncBindingService {

    private JobProgressService jobProgressService;

    public AsyncBindingServiceImpl(JobProgressService jobProgressService) {
        super(jobProgressService);
        this.jobProgressService = jobProgressService;
    }

    @Async
    @Override
    public void asyncCreateServiceInstanceBinding(BindingServiceImpl bindingService, String bindingId,
                                                  ServiceInstance serviceInstance, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                  Plan plan, boolean async, String jobProgressId) {
        try {
            JobProgress jobProgress = jobProgressService.startJob(jobProgressId, bindingId,
                    "Creating binding..", JobProgress.BIND);
            bindingService.createBinding(bindingId, serviceInstance, serviceInstanceBindingRequest, plan);
            jobProgressService.succeedProgress(jobProgress.getId(), "Instance Binding successfully created");
        } catch (Exception e) {
            logException(jobProgressId, "binding creation", e);
        }
    }

    @Async
    @Override
    public void asyncDeleteServiceInstanceBinding(BindingServiceImpl bindingServiceImpl, String bindingId,
                                                  ServiceInstance serviceInstance, Plan plan, String jobProgressId) {
        try {
            jobProgressService.startJob(jobProgressId, bindingId, "Deleting binding..", JobProgress.UNBIND);
            bindingServiceImpl.deleteServiceInstanceBinding(bindingId, serviceInstance, plan);
        } catch (Exception e) {
            logException(jobProgressId, "binding deletion", e);
        }
    }
}