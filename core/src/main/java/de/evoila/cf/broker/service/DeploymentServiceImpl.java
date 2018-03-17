/**
 * 
 */
package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.repository.JobRepository;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Brinker.
 *
 */
@Service
public class DeploymentServiceImpl implements DeploymentService {

    Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private PlatformRepository platformRepository;

	@Autowired
    private ServiceDefinitionRepository serviceDefinitionRepository;

	@Autowired
	private ServiceInstanceRepository serviceInstanceRepository;
	
	@Autowired
	private JobRepository jobRepository;

	@Autowired(required = false)
	private AsyncDeploymentService asyncDeploymentService;

	@Override
	public JobProgressResponse getLastOperation(String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException {
		JobProgress progress = asyncDeploymentService.getProgress(serviceInstanceId);

		if (progress == null || !serviceInstanceRepository.containsServiceInstanceId(serviceInstanceId)) {
			throw new ServiceInstanceDoesNotExistException("Service instance not found " + serviceInstanceId);
		}

		return new JobProgressResponse(progress);
	}

	@Override
	public ServiceInstanceResponse createServiceInstance(String serviceInstanceId, String serviceDefinitionId,
			String planId, String organizationGuid, String spaceGuid, Map<String, String> parameters,
			Map<String, String> context)
					throws ServiceInstanceExistsException, ServiceBrokerException,
					ServiceDefinitionDoesNotExistException {

		serviceDefinitionRepository.validateServiceId(serviceDefinitionId);

		if (serviceInstanceRepository.containsServiceInstanceId(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId, serviceDefinitionId);
		}

		ServiceInstance serviceInstance = new ServiceInstance(serviceInstanceId, serviceDefinitionId,
				planId, organizationGuid, spaceGuid,
				parameters == null ? new HashMap<>()
						: new HashMap<>(parameters),
				context == null ? new HashMap<>()
						: new HashMap<>(context));

		Plan plan = serviceDefinitionRepository.getPlan(planId);

		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		if(platformService == null) {
			throw new ServiceDefinitionDoesNotExistException(planId);
		}
		
		if (platformService.isSyncPossibleOnCreate(plan)) {
			return new ServiceInstanceResponse(syncCreateInstance(serviceInstance, parameters, plan, platformService), false);
		} else {
			ServiceInstanceResponse serviceInstanceResponse = new ServiceInstanceResponse(serviceInstance, true);

			serviceInstanceRepository.addServiceInstance(serviceInstance.getId(), serviceInstance);

			asyncDeploymentService.asyncCreateInstance(this, serviceInstance, parameters, plan, platformService);

			return serviceInstanceResponse;
		}
	}

    /*
     * (non-Javadoc)
     *
     * @see
     * de.evoila.cf.broker.service.ServiceInstanceService#deleteInstance(
     * java.lang.String)
     */
    @Override
    public void deleteServiceInstance(String instanceId)
            throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);

        if (serviceInstance == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }

        Plan plan = serviceDefinitionRepository.getPlan(serviceInstance.getPlanId());

        PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

        if (platformService.isSyncPossibleOnDelete(serviceInstance)
                && platformService.isSyncPossibleOnDelete(serviceInstance)) {
            syncDeleteInstance(serviceInstance, platformService);
        } else {
            asyncDeploymentService.asyncDeleteInstance(this, instanceId, serviceInstance, platformService);
        }
    }

	public ServiceInstance syncCreateInstance(ServiceInstance serviceInstance, Map<String, String> parameters,
                                                       Plan plan, PlatformService platformService) throws ServiceBrokerException {

	    // TODO: We need to decide which method we trigger when preCreateInstance fails
        try {
            serviceInstance = platformService.preCreateInstance(serviceInstance, plan);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during pre service instance creation", e);
        }

		try {
            serviceInstance = platformService.createInstance(serviceInstance, plan, parameters);
		} catch (PlatformException e) {
			serviceInstanceRepository.deleteServiceInstance(serviceInstance.getId());

			throw new ServiceBrokerException("Could not create instance due to: ", e);
		}

		try {
            serviceInstance = platformService.postCreateInstance(serviceInstance, plan);
		} catch (PlatformException e) {
			throw new ServiceBrokerException("Error during post service instance creation", e);
		}

		serviceInstanceRepository.addServiceInstance(serviceInstance.getId(), serviceInstance);

		return serviceInstance;
	}



	public void syncDeleteInstance(ServiceInstance serviceInstance, PlatformService platformService)
			throws ServiceBrokerException {

        try {
            platformService.preDeleteInstance(serviceInstance);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during pre service instance deletion", e);
        }

		try {
			platformService.deleteInstance(serviceInstance);
		} catch (PlatformException e) {
			throw new ServiceBrokerException("Error during deletion of service", e);
		}

        try {
            platformService.postDeleteInstance(serviceInstance);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during pre service instance deletion", e);
        }

        serviceInstanceRepository.deleteServiceInstance(serviceInstance.getId());
        jobRepository.deleteJobProgress(serviceInstance.getId());
	}

}
