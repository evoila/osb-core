package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.BindingService;
import de.evoila.cf.broker.service.JobProgressService;
import de.evoila.cf.broker.service.PlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Marco Di Martino
 */

@Service
public class AsyncBindingServiceImpl implements AsyncBindingService {

    Logger log = LoggerFactory.getLogger(AsyncBindingServiceImpl.class);

    private JobProgressService jobProgressService;

    public AsyncBindingServiceImpl(JobProgressService jobProgressService) {
        this.jobProgressService= jobProgressService;
    }

    @Async
    @Override
    public ServiceInstanceBindingResponse asyncCreateServiceInstanceBinding(BindingServiceImpl bindingService, String bindingId, ServiceInstance serviceInstance,
                                                                            ServiceInstanceBindingRequest serviceInstanceBindingRequest, Plan plan, boolean async)  {
        jobProgressService.startJob(bindingId, "Start creating binding..", JobProgress.BIND);
        ServiceInstanceBindingResponse response;
        try {
            response = bindingService.syncCreateBinding(bindingId, serviceInstance, serviceInstanceBindingRequest, plan, async);
        } catch (Exception e) {
            jobProgressService.failJob(bindingId,
                    "Internal error during binding creation, please contact our support.");

            log.error("Exception during Binding creation", e);
            return null;
        }
        jobProgressService.succeedProgress(bindingId, "Instance Binding successfully created");
        return response;
    }

    @Async
    @Override
    public void asyncDeleteServiceInstanceBinding(BindingServiceImpl bindingServiceImpl, String bindingId,
                                                  ServiceInstance serviceInstance, Plan plan) {
        jobProgressService.startJob(serviceInstance, "Start deleting binding..", JobProgress.DELETE);

        try {
            bindingServiceImpl.syncDeleteServiceInstanceBinding(bindingId, serviceInstance, plan);

        } catch (Exception e) {
            jobProgressService.failJob(serviceInstance,
                    "Internal error during binding deletion, please contact our support.");

            log.error("Exception during binding deletion", e);
            return;
        }
        jobProgressService.succeedProgress(serviceInstance, "Instance Binding successfully deleted");
    }


    public JobProgress getProgress(String bindingId){
        try {
            return jobProgressService.getProgress(bindingId);
        } catch (Exception e) {
            log.error("Error during job progress retrieval", e);
            return new JobProgress(JobProgress.UNKNOWN, JobProgress.UNKNOWN, "Error during job progress retrieval");
        }
    }
}
