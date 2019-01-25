package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.impl.DeploymentServiceImpl;

import java.util.Map;

/**
 * @author Dennis Mueller, Johannes Hiemer.
 **/
public interface AsyncDeploymentService extends AsyncOperationService {

    void asyncCreateInstance(DeploymentServiceImpl deploymentService, ServiceInstance serviceInstance,
                             Map<String, Object> parameters, Plan plan, PlatformService platformService, String jobProgressId);

    void asyncUpdateInstance(DeploymentServiceImpl deploymentService, ServiceInstance serviceInstance,
                             Map<String, Object> parameters, Plan plan, PlatformService platformService, String jobProgressId);

	void asyncDeleteInstance(DeploymentServiceImpl deploymentService,
			ServiceInstance serviceInstance, Plan plan, PlatformService platformService, String jobProgressId)
					throws ServiceInstanceDoesNotExistException;

}
