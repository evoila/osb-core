/**
 *
 */
package de.evoila.cf.broker.service.impl;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.RouteBinding;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.RouteBindingRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.repository.*;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.BindingService;
import de.evoila.cf.broker.service.HAProxyService;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.util.ParameterValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/** @author Johannes Hiemer, Marco Di Martino. */
@Service
public abstract class BindingServiceImpl implements BindingService {

	private final Logger log = LoggerFactory.getLogger(BindingServiceImpl.class);

	protected BindingRepository bindingRepository;

	protected ServiceDefinitionRepository serviceDefinitionRepository;

	protected ServiceInstanceRepository serviceInstanceRepository;

	protected RouteBindingRepository routeBindingRepository;

	protected HAProxyService haProxyService;

	protected JobRepository jobRepository;

	protected AsyncBindingService asyncBindingService;

	protected PlatformRepository platformRepository;


	public BindingServiceImpl(BindingRepository bindingRepository, ServiceDefinitionRepository serviceDefinitionRepository,
							  ServiceInstanceRepository serviceInstanceRepository, RouteBindingRepository routeBindingRepository,
							  HAProxyService haProxyService, JobRepository jobRepository, AsyncBindingService asyncBindingService,
							  PlatformRepository platformRepository) {
		this.bindingRepository = bindingRepository;
		this.serviceDefinitionRepository = serviceDefinitionRepository;
		this.serviceInstanceRepository = serviceInstanceRepository;
		this.routeBindingRepository = routeBindingRepository;
		this.haProxyService = haProxyService;
		this.jobRepository = jobRepository;
		this.asyncBindingService = asyncBindingService;
		this.platformRepository = platformRepository;
	}

	@Override
	public ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			ServiceInstanceBindingRequest serviceInstanceBindingRequest, boolean async) throws ServiceInstanceBindingExistsException,
			ServiceBrokerException, ServiceDefinitionDoesNotExistException, ServiceInstanceDoesNotExistException,
			InvalidParametersException, AsyncRequiredException{

		validateBindingNotExists(bindingId, instanceId);

		ServiceInstance serviceInstance;
		try {
			serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);
		} catch(Exception e) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		Plan plan = serviceDefinitionRepository.getPlan(serviceInstanceBindingRequest.getPlanId());
		if (serviceInstanceBindingRequest.getParameters() != null && serviceInstanceBindingRequest.getParameters().size() > 0) {
			try {
				ParameterValidator.validateParameters(serviceInstanceBindingRequest, plan);
			} catch(ProcessingException e) {
			throw new InvalidParametersException("Error while validating parameters");
			}
		}
		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		if (!platformService.isSyncPossibleOnBind() && !async) {
			throw new AsyncRequiredException();
		}
		if (platformService.isSyncPossibleOnBind() && !async) {
			return syncCreateBinding(bindingId, serviceInstance, serviceInstanceBindingRequest, plan, async);
		}else if (async){
			bindingRepository.addInternalBinding(new ServiceInstanceBinding(bindingId, instanceId, null));
			return asyncBindingService.asyncCreateServiceInstanceBinding(this, bindingId, serviceInstance, serviceInstanceBindingRequest, plan, async);
		}else
			throw new ServiceInstanceBindingBadRequestException(bindingId);
	}

	protected abstract RouteBinding bindRoute(ServiceInstance serviceInstance, String route);

	protected ServiceInstanceBinding createServiceInstanceBinding(String bindingId, String serviceInstanceId,
																  Map<String, Object> credentials, String syslogDrainUrl, String appGuid) {
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId, serviceInstanceId, credentials,
				syslogDrainUrl);
		return binding;
	}

	@Override
	public void deleteServiceInstanceBinding(String bindingId, String planId, boolean async)
			throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceInstanceBindingBadRequestException {
		ServiceInstance serviceInstance = getBinding(bindingId);
		Plan plan = serviceDefinitionRepository.getPlan(planId);
		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		if (!platformService.isSyncPossibleOnUnbind() && !async) {
			throw new AsyncRequiredException();
		}

		if (platformService.isSyncPossibleOnUnbind() && !async) {
			syncDeleteServiceInstanceBinding(bindingId, serviceInstance, plan);
		} else if (async) {
			asyncBindingService.asyncDeleteServiceInstanceBinding(this, bindingId, serviceInstance, plan);
		} else
			throw new ServiceInstanceBindingBadRequestException(bindingId);

	}

	@Override
	public ServiceInstanceBinding fetchServiceInstanceBinding(String bindingId, String instanceId) throws ServiceInstanceBindingNotFoundException {

		ServiceInstanceBinding serviceInstanceBinding;
		try {
			serviceInstanceBinding = bindingRepository.findOne(bindingId);
		} catch(Exception e) {
			throw new ServiceInstanceBindingNotFoundException();
		}

		boolean isBindingInProgress = false;
		if (jobRepository.containsJobProgress(bindingId)){
			JobProgress job = jobRepository.getJobProgress(bindingId);
			isBindingInProgress = job.getOperation().equals(JobProgress.BIND) && job.getState().equals(JobProgress.IN_PROGRESS);
		}

		if (isBindingInProgress) {
			throw new ServiceInstanceBindingNotFoundException();
		}
		return serviceInstanceBinding;
	}

	@Override
	public JobProgressResponse getLastOperation(String bindingId)
			throws  ServiceInstanceBindingDoesNotExistsException {
		JobProgress progress = asyncBindingService.getProgress(bindingId);

		if (progress == null || !bindingRepository.containsInternalBindingId(bindingId)) {
			throw new ServiceInstanceBindingDoesNotExistsException(bindingId);
		}

		return new JobProgressResponse(progress);
	}

	public ServiceInstanceBindingResponse syncCreateBinding(String bindingId, ServiceInstance serviceInstance, ServiceInstanceBindingRequest serviceInstanceBindingRequest, Plan plan, boolean async)
			throws ServiceBrokerException, InvalidParametersException {
		String instanceId = serviceInstance.getId();

		if (serviceInstanceBindingRequest.getBindResource() != null && !StringUtils
				.isEmpty(serviceInstanceBindingRequest.getBindResource().getRoute())) {

			RouteBinding routeBinding = bindRoute(serviceInstance, serviceInstanceBindingRequest.getBindResource().getRoute());
			routeBindingRepository.addRouteBinding(routeBinding);
			ServiceInstanceBindingResponse response = new ServiceInstanceBindingResponse(routeBinding.getRoute(), async);
			return response;
		}

		ServiceInstanceBinding binding;
		if (haProxyService != null && serviceInstanceBindingRequest.getAppGuid() == null &&
					(serviceInstanceBindingRequest.getBindResource() != null && serviceInstanceBindingRequest.getBindResource().getAppGuid() == null)) {
			List<ServerAddress> externalServerAddresses = haProxyService.appendAgent(serviceInstance.getHosts(), bindingId, instanceId);

			binding = bindServiceKey(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, externalServerAddresses);
		} else {
			binding = bindService(bindingId, serviceInstanceBindingRequest, serviceInstance, plan);
		}

		bindingRepository.addInternalBinding(binding);

		return new ServiceInstanceBindingResponse(binding, async);
	}

	public void syncDeleteServiceInstanceBinding(String bindingId, ServiceInstance serviceInstance, Plan plan){
		try {
			ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
			List<ServerAddress> externalServerAddresses = binding.getExternalServerAddresses();
			if (externalServerAddresses != null && haProxyService != null) {
				haProxyService.removeAgent(serviceInstance.getHosts(), bindingId);
			}

			unbindService(binding, serviceInstance, plan);
		} catch (ServiceBrokerException e) {
			log.error("Could not cleanup service binding", e);
		} finally {
			bindingRepository.unbindService(bindingId);
			jobRepository.deleteJobProgress(bindingId);
		}
	}

	protected ServiceInstance getBinding(String bindingId) throws ServiceInstanceBindingDoesNotExistsException {
		if (!bindingRepository.containsInternalBindingId(bindingId)) {
			throw new ServiceInstanceBindingDoesNotExistsException(bindingId);
		}
		String serviceInstanceId = bindingRepository.getInternalBindingId(bindingId);
		if (serviceInstanceId == null) {
			throw new ServiceInstanceBindingDoesNotExistsException(bindingId);
		}
		return serviceInstanceRepository.getServiceInstance(serviceInstanceId);
	}

	protected ServiceInstanceBinding bindServiceKey(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                    ServiceInstance serviceInstance, Plan plan, List<ServerAddress> externalAddresses) throws ServiceBrokerException, InvalidParametersException {
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, externalAddresses.get(0));

		ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(),
				credentials);
		serviceInstanceBinding.setExternalServerAddresses(externalAddresses);
		return serviceInstanceBinding;
	}

	protected ServiceInstanceBinding bindService(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                 ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException, InvalidParametersException {
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, null);

		return new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials);
	}

    protected abstract void unbindService(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan)
            throws ServiceBrokerException;

	protected abstract Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                             ServiceInstance serviceInstance, Plan plan, ServerAddress serverAddress) throws ServiceBrokerException, InvalidParametersException;

    protected void validateBindingNotExists(String bindingId, String instanceId)
            throws ServiceInstanceBindingExistsException {

    	boolean bindCreation;
		boolean isBindingInProgress;

		if (bindingRepository.containsInternalBindingId(bindingId)) {
    		try {
    			bindCreation = jobRepository.getJobProgress(bindingId).getOperation().equals(JobProgress.BIND);
    			isBindingInProgress = jobRepository.getJobProgress(bindingId).getDescription().equals(JobProgress.IN_PROGRESS);
			 } catch (NoSuchElementException e) {
    			return;
			}
    		if (bindCreation && !isBindingInProgress)
            	throw new ServiceInstanceBindingExistsException(bindingId, instanceId);
        }
    }

    public ServiceInstance getServiceInstance(String instanceId) throws ServiceInstanceDoesNotExistException{
    	ServiceInstance serviceInstance;
    	try {
    		serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);
		} catch(Exception e) {
    		throw new ServiceInstanceDoesNotExistException(instanceId);
		}
		return serviceInstance;
    }
}