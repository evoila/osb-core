package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.JobProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Marco Di Martino, Johannes Hiemer.
 **/
@Service
public class AsyncBindingServiceImpl extends AsyncOperationServiceImpl implements AsyncBindingService {

    Logger log = LoggerFactory.getLogger(AsyncBindingServiceImpl.class);

    private JobProgressService jobProgressService;

    public AsyncBindingServiceImpl(JobProgressService jobProgressService) {
        super(jobProgressService);
        this.jobProgressService= jobProgressService;
    }

    @Async
    @Override
    public void asyncCreateServiceInstanceBinding(BindingServiceImpl bindingService, String bindingId,
                    ServiceInstance serviceInstance, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                    Plan plan, boolean async, String jobProgressId)  {
        JobProgress jobProgress = jobProgressService.startJob(jobProgressId, bindingId,
                "Creating binding..", JobProgress.BIND);
        ServiceInstanceBindingResponse response;
        try {
            response = bindingService.syncCreateBinding(bindingId, serviceInstance, serviceInstanceBindingRequest, plan);
        } catch (Exception e) {
            jobProgressService.failJob(jobProgress.getId(),
                    "Internal error during binding creation, please contact our support.");

            log.error("Exception during Binding creation", e);
            return;
        }
        jobProgressService.succeedProgress(jobProgress.getId(), "Instance Binding successfully created");
    }

    @Async
    @Override
    public void asyncDeleteServiceInstanceBinding(BindingServiceImpl bindingServiceImpl, String bindingId,
                                                  ServiceInstance serviceInstance, Plan plan, String jobProgressId) {
        JobProgress jobProgress = jobProgressService.startJob(jobProgressId, bindingId,
                "Deleting binding..", JobProgress.UNBIND);
        try {
            bindingServiceImpl.syncDeleteServiceInstanceBinding(bindingId, serviceInstance, plan);

        } catch (Exception e) {
            jobProgressService.failJob(jobProgress.getId(),
                    "Internal error during binding deletion, please contact our support.");

            log.error("Exception during binding deletion", e);
            return;
        }
    }

}
