package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.AsyncDeploymentService;
import de.evoila.cf.broker.service.JobProgressService;
import de.evoila.cf.broker.service.PlatformService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
@Service
public class AsyncDeploymentServiceImpl extends AsyncOperationServiceImpl implements AsyncDeploymentService {

	public AsyncDeploymentServiceImpl(JobProgressService progressService) {
		super(progressService);
	    this.progressService = progressService;
	}

	@Async
	@Override
	public void asyncCreateInstance(DeploymentServiceImpl deploymentService, ServiceInstance serviceInstance,
                                    Map<String, Object> parameters, Plan plan, PlatformService platformService, String jobProgressId) {
        try {
            JobProgress jobProgress = progressService.startJob(jobProgressId, serviceInstance.getId(),
                    "Creating service..", JobProgress.PROVISION);
            deploymentService.syncCreateInstance(serviceInstance, parameters, plan, platformService);
            progressService.succeedProgress(jobProgress.getId(), "Instance successfully created");
        } catch (ServiceBrokerException e) {
            log.error("Exception during Instance creation", e);
        } catch (Exception e) {
            logUnexpectedException(jobProgressId, "creation", e);
        }
	}

    @Async
    @Override
    public void asyncUpdateInstance(DeploymentServiceImpl deploymentService, ServiceInstance serviceInstance,
                                    Map<String, Object> parameters, Plan plan, PlatformService platformService, String jobProgressId) {
        try {
            progressService.startJob(jobProgressId, serviceInstance.getId(),
                    "Updating service..", JobProgress.UPDATE);
            deploymentService.syncUpdateInstance(serviceInstance, parameters, plan, platformService);
            progressService.succeedProgress(jobProgressId, "Instance successfully updated");
        } catch (ServiceBrokerException e) {
            log.error("Exception during instance update", e);
        } catch (Exception e) {
            logUnexpectedException(jobProgressId, "update", e);
        }
    }

	@Async
	@Override
	public void asyncDeleteInstance(DeploymentServiceImpl deploymentService,
			ServiceInstance serviceInstance, Plan plan, PlatformService platformService, String jobProgressId) {
        try {
            progressService.startJob(jobProgressId, serviceInstance.getId(),
                    "Deleting service..", JobProgress.DELETE);
            deploymentService.syncDeleteInstance(serviceInstance, plan, platformService);

        } catch (ServiceBrokerException e) {
            log.error("Exception during instance deletion, while saving new JobProgress object.", e);
        } catch (Exception e) {
            logUnexpectedException(jobProgressId, "deletion", e);
        }
    }

}
