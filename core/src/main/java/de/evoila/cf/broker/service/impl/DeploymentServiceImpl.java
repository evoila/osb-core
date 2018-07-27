/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.repository.JobRepository;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.AsyncDeploymentService;
import de.evoila.cf.broker.service.DeploymentService;
import de.evoila.cf.broker.service.PlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
	public ServiceInstanceResponse createServiceInstance(String serviceInstanceId, ServiceInstanceRequest request, List<Map<String, Object>> extension_apis)
					throws ServiceInstanceExistsException, ServiceBrokerException,
					ServiceDefinitionDoesNotExistException {

		serviceDefinitionRepository.validateServiceId(request.getServiceDefinitionId());

		if (serviceInstanceRepository.containsServiceInstanceId(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId, request.getServiceDefinitionId());
		}

		ServiceInstance serviceInstance = new ServiceInstance(serviceInstanceId, request.getServiceDefinitionId(),
                request.getPlanId(), request.getOrganizationGuid(), request.getSpaceGuid(), request.getParameters(), request.getContext());

		Plan plan = serviceDefinitionRepository.getPlan(request.getPlanId());

		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		if (platformService == null) {
			throw new ServiceBrokerException("Not Platform configured for " + plan.getPlatform());
		}
		
		if (platformService.isSyncPossibleOnCreate(plan)) {
			return new ServiceInstanceResponse(syncCreateInstance(serviceInstance, request.getParameters(), plan, platformService), false, extension_apis);
		} else {
			ServiceInstanceResponse serviceInstanceResponse = new ServiceInstanceResponse(serviceInstance, true, extension_apis);

			serviceInstanceRepository.addServiceInstance(serviceInstance.getId(), serviceInstance);

			asyncDeploymentService.asyncCreateInstance(this, serviceInstance, request.getParameters(), plan, platformService);

			return serviceInstanceResponse;
		}
	}

    @Override
    public void updateServiceInstance(String serviceInstanceId, ServiceInstanceRequest request) throws ServiceBrokerException, ServiceInstanceDoesNotExistException,
            ServiceDefinitionDoesNotExistException {

        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
        if (serviceInstance == null){
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }

        Plan plan = serviceDefinitionRepository.getPlan(request.getPlanId());

        PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

        if(platformService == null) {
            throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
        }

        if (platformService.isSyncPossibleOnCreate(plan)) {
            syncUpdateInstance(serviceInstance, request.getParameters(), plan, platformService);
        } else {
            asyncDeploymentService.asyncUpdateInstance(this, serviceInstance, request.getParameters(), plan, platformService);
        }
    }

    @Override
    public void deleteServiceInstance(String instanceId)
            throws ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);

        if (serviceInstance == null) {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }

        Plan plan = serviceDefinitionRepository.getPlan(serviceInstance.getPlanId());

        PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

        if (platformService.isSyncPossibleOnDelete(serviceInstance)
                && platformService.isSyncPossibleOnDelete(serviceInstance)) {
            syncDeleteInstance(serviceInstance, plan, platformService);
        } else {
            asyncDeploymentService.asyncDeleteInstance(this, serviceInstance, plan, platformService);
        }
    }

	public ServiceInstance syncCreateInstance(ServiceInstance serviceInstance, Map<String, Object> parameters,
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

    public ServiceInstance syncUpdateInstance(ServiceInstance serviceInstance, Map<String, Object> parameters,
                                              Plan plan, PlatformService platformService) throws ServiceBrokerException {

        // TODO: We need to decide which method we trigger when preCreateInstance fails
        try {
            serviceInstance = platformService.preUpdateInstance(serviceInstance, plan);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during pre service instance update", e);
        }

        try {
            serviceInstance = platformService.updateInstance(serviceInstance, plan, parameters);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Could not update instance due to: ", e);
        }

        try {
            serviceInstance = platformService.postUpdateInstance(serviceInstance, plan);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during post service instance update", e);
        }

        serviceInstanceRepository.updateServiceInstance(serviceInstance);

        return serviceInstance;
    }

	public void syncDeleteInstance(ServiceInstance serviceInstance, Plan plan, PlatformService platformService)
			throws ServiceBrokerException {
 
        try {
            platformService.preDeleteInstance(serviceInstance);
        } catch (PlatformException e) {
            throw new ServiceBrokerException("Error during pre service instance deletion", e);
        }

		try {
			platformService.deleteInstance(serviceInstance, plan);
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
