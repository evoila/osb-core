package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

import java.util.Map;

/**
 * 
 * @author Dennis Mueller, evoila GmbH, Sep 9, 2015
 *
 */

public interface AsyncDeploymentService {

	void asyncCreateInstance(DeploymentServiceImpl deploymentService, ServiceInstance serviceInstance,
			Map<String, String> parameters, Plan plan, PlatformService platformService);

	void asyncDeleteInstance(DeploymentServiceImpl deploymentService,
			ServiceInstance serviceInstance, Plan plan, PlatformService platformService)
					throws ServiceInstanceDoesNotExistException;

	JobProgress getProgress(String serviceInstanceId);
}
