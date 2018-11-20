package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.impl.DeploymentServiceImpl;

import java.util.Map;

/**
 * 
 * @author Dennis Mueller, evoila GmbH, Sep 9, 2015
 *
 */

public interface AsyncDeploymentService {

	void asyncCreateInstance(DeploymentServiceImpl deploymentService, ServiceInstance serviceInstance,
                             Map<String, Object> parameters, Plan plan, PlatformService platformService);

    void asyncUpdateInstance(DeploymentServiceImpl deploymentService, ServiceInstance serviceInstance,
                             Map<String, Object> parameters, Plan plan, PlatformService platformService);

	void asyncDeleteInstance(DeploymentServiceImpl deploymentService,
			ServiceInstance serviceInstance, Plan plan, PlatformService platformService)
					throws ServiceInstanceDoesNotExistException;

	JobProgress getProgress(String serviceInstanceId);
}
