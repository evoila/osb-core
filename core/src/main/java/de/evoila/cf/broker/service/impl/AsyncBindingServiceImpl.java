package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.BindingService;
import de.evoila.cf.broker.service.JobProgressService;
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
                    "Internal error during Binding creation, please contact our support.");

            log.error("Exception during Binding creation", e);
            return null;
        }
        jobProgressService.succeedProgress(bindingId, "Instance Binding successfully created");
        return response;
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
