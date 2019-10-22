package de.evoila.cf.broker.service.impl;

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
        JobProgress jobProgress = progressService.startJob(jobProgressId, serviceInstance.getId(),
                "Creating service..", JobProgress.PROVISION);
        if (jobProgress == null) {
            return;
        }
		try {
            deploymentService.syncCreateInstance(serviceInstance, parameters, plan, platformService);
		} catch (Exception e) {
            progressService.failJob(jobProgress.getId(),
                    "Internal error during Instance creation, please contact our support.");

            log.error("Exception during Instance creation", e);
            return;
        }
		progressService.succeedProgress(jobProgress.getId(), "Instance successfully created");
	}

    @Async
    @Override
    public void asyncUpdateInstance(DeploymentServiceImpl deploymentService, ServiceInstance serviceInstance,
                                    Map<String, Object> parameters, Plan plan, PlatformService platformService, String jobProgressId) {
        JobProgress jobProgress = progressService.startJob(jobProgressId, serviceInstance.getId(),
                "Updating service..", JobProgress.UPDATE);
        if (jobProgress == null) {
            return;
        }
        try {
            deploymentService.syncUpdateInstance(serviceInstance, parameters, plan, platformService);
        } catch (Exception e) {
            progressService.failJob(jobProgress.getId(),
                    "Internal error during Instance creation, please contact our support.");

            log.error("Exception during Instance creation", e);
            return;
        }
        progressService.succeedProgress(jobProgress.getId(), "Instance successfully updated");
    }

	@Async
	@Override
	public void asyncDeleteInstance(DeploymentServiceImpl deploymentService,
			ServiceInstance serviceInstance, Plan plan, PlatformService platformService, String jobProgressId) {
        JobProgress jobProgress = progressService.startJob(jobProgressId, serviceInstance.getId(),
                "Deleting service..", JobProgress.DELETE);
        if (jobProgress == null) {
            return;
        }
		try {
            deploymentService.syncDeleteInstance(serviceInstance, plan, platformService);

		} catch(Exception e) {
			progressService.failJob(jobProgress.getId(),
					"Internal error during Instance deletion, please contact our support.");

			log.error("Exception during Instance deletion", e);
			return;
		}
	}

}
