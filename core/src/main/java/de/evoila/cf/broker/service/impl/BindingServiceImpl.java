package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.*;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.BindingService;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.util.ParameterValidator;
import de.evoila.cf.security.utils.RandomString;
import org.everit.json.schema.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Johannes Hiemer, Marco Di Martino.
 **/
@Service
public abstract class BindingServiceImpl implements BindingService {

	private final Logger log = LoggerFactory.getLogger(BindingServiceImpl.class);

	protected BindingRepository bindingRepository;

	protected ServiceDefinitionRepository serviceDefinitionRepository;

	protected ServiceInstanceRepository serviceInstanceRepository;

	protected RouteBindingRepository routeBindingRepository;

	protected JobRepository jobRepository;

	protected AsyncBindingService asyncBindingService;

	protected PlatformRepository platformRepository;

	private RandomString randomString = new RandomString();


	public BindingServiceImpl(BindingRepository bindingRepository, ServiceDefinitionRepository serviceDefinitionRepository,
							  ServiceInstanceRepository serviceInstanceRepository, RouteBindingRepository routeBindingRepository,
							  JobRepository jobRepository, AsyncBindingService asyncBindingService, PlatformRepository platformRepository) {
		this.bindingRepository = bindingRepository;
		this.serviceDefinitionRepository = serviceDefinitionRepository;
		this.serviceInstanceRepository = serviceInstanceRepository;
		this.routeBindingRepository = routeBindingRepository;
		this.jobRepository = jobRepository;
		this.asyncBindingService = asyncBindingService;
		this.platformRepository = platformRepository;
	}

	@Override
	public BaseServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			ServiceInstanceBindingRequest serviceInstanceBindingRequest, boolean async) throws ServiceInstanceBindingExistsException,
			ServiceBrokerException, ServiceDefinitionDoesNotExistException, ServiceInstanceDoesNotExistException,
			InvalidParametersException, AsyncRequiredException, ValidationException, PlatformException  {

		validateBindingNotExists(bindingId, instanceId);

		ServiceInstance serviceInstance;
		try {
			serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);
		} catch(Exception e) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		Plan plan = serviceDefinitionRepository.getPlan(serviceInstanceBindingRequest.getPlanId());
		if (serviceInstanceBindingRequest.getParameters() != null) {
		    ParameterValidator.validateParameters(serviceInstanceBindingRequest, plan, false);
		}

		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		BaseServiceInstanceBindingResponse baseServiceInstanceBindingResponse;

		if (async) {
			if (platformService.isSyncPossibleOnBind()) {
				baseServiceInstanceBindingResponse = syncCreateBinding(bindingId, serviceInstance,
						serviceInstanceBindingRequest, plan);
			} else {
				bindingRepository.addInternalBinding(new ServiceInstanceBinding(bindingId, instanceId, null));

				String operationId = randomString.nextString();

				asyncBindingService.asyncCreateServiceInstanceBinding(this, bindingId,
						serviceInstance, serviceInstanceBindingRequest, plan, async, operationId);

				baseServiceInstanceBindingResponse = new ServiceInstanceBindingOperationResponse(operationId);
			}
		} else {
			if (!platformService.isSyncPossibleOnBind()) {
				throw new AsyncRequiredException();
			} else {
				baseServiceInstanceBindingResponse = syncCreateBinding(bindingId, serviceInstance,
						serviceInstanceBindingRequest, plan);
			}
		}
		return baseServiceInstanceBindingResponse;
	}

	protected abstract RouteBinding bindRoute(ServiceInstance serviceInstance, String route);

	protected ServiceInstanceBinding createServiceInstanceBinding(String bindingId, String serviceInstanceId,
																  Map<String, Object> credentials, String syslogDrainUrl, String appGuid) {
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId, serviceInstanceId, credentials,
				syslogDrainUrl);
		return binding;
	}

	@Override
	public BaseServiceInstanceBindingResponse deleteServiceInstanceBinding(String bindingId, String planId, boolean async)
			throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException,
            AsyncRequiredException, ServiceInstanceBindingBadRequestException {
		ServiceInstance serviceInstance = getBinding(bindingId);
		Plan plan = serviceDefinitionRepository.getPlan(planId);
		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		if (async) {
			if (platformService.isSyncPossibleOnUnbind()) {
				syncDeleteServiceInstanceBinding(bindingId, serviceInstance, plan);
			} else {
				String operationId = randomString.nextString();

				asyncBindingService.asyncDeleteServiceInstanceBinding(this, bindingId, serviceInstance,
						plan, operationId);

				return new ServiceInstanceBindingOperationResponse(operationId);
			}
		} else {
			if (!platformService.isSyncPossibleOnUnbind()) {
				throw new AsyncRequiredException();
			} else {
				syncDeleteServiceInstanceBinding(bindingId, serviceInstance, plan);
			}

		}
		return null;
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
			JobProgress job = jobRepository.getJobProgressByReferenceId(bindingId);
			isBindingInProgress = job.getOperation().equals(JobProgress.BIND) && job.getState().equals(JobProgress.IN_PROGRESS);
		}

		if (isBindingInProgress) {
			throw new ServiceInstanceBindingNotFoundException();
		}
		return serviceInstanceBinding;
	}

	@Override
	public JobProgressResponse getLastOperationByReferenceId(String referenceId)
			throws  ServiceInstanceBindingDoesNotExistsException {
		JobProgress progress = asyncBindingService.getProgressByReferenceId(referenceId);

		if (progress == null || !bindingRepository.containsInternalBindingId(referenceId)) {
			throw new ServiceInstanceBindingDoesNotExistsException(referenceId);
		}

		return new JobProgressResponse(progress);
	}

    @Override
    public JobProgressResponse getLastOperationById(String referenceId, String jobProgressId)
            throws  ServiceInstanceBindingDoesNotExistsException {
        JobProgress progress = asyncBindingService.getProgressById(jobProgressId);

        if (progress == null || !bindingRepository.containsInternalBindingId(referenceId)) {
            throw new ServiceInstanceBindingDoesNotExistsException(referenceId);
        }

        return new JobProgressResponse(progress);
    }

	public ServiceInstanceBindingResponse syncCreateBinding(String bindingId, ServiceInstance serviceInstance,
                                                            ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                            Plan plan)
			throws ServiceBrokerException, InvalidParametersException, PlatformException{

        ServiceInstanceBindingResponse serviceInstanceBindingResponse;
		if (serviceInstanceBindingRequest.getBindResource() != null && !StringUtils
				.isEmpty(serviceInstanceBindingRequest.getBindResource().getRoute())) {

			RouteBinding routeBinding = bindRoute(serviceInstance, serviceInstanceBindingRequest.getBindResource().getRoute());
			routeBindingRepository.addRouteBinding(routeBinding);
			serviceInstanceBindingResponse = new ServiceInstanceBindingResponse(routeBinding.getRoute());

			return serviceInstanceBindingResponse;
		}

		ServiceInstanceBinding binding = bindService(bindingId, serviceInstanceBindingRequest, serviceInstance, plan);

		bindingRepository.addInternalBinding(binding);
        serviceInstanceBindingResponse = new ServiceInstanceBindingResponse(binding);

		return serviceInstanceBindingResponse;
	}

	public void syncDeleteServiceInstanceBinding(String bindingId, ServiceInstance serviceInstance, Plan plan) {
		try {
			ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
			unbindService(binding, serviceInstance, plan);
		} catch (ServiceBrokerException | PlatformException e) {
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
		ServiceInstance serviceInstance;
		try {
		serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

		}catch (ServiceInstanceDoesNotExistException e){
			log.error("Service Instance does not exist!", e);
			throw new ServiceInstanceBindingDoesNotExistsException(bindingId);
		}

		return serviceInstance;
	}

	protected ServiceInstanceBinding bindService(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
												 ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException, InvalidParametersException, PlatformException {
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstanceBindingRequest, serviceInstance, plan, null);

		return new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials);
	}

	protected abstract void unbindService(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException, PlatformException;

	protected abstract Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                             ServiceInstance serviceInstance, Plan plan,
                                                             ServerAddress serverAddress) throws ServiceBrokerException, InvalidParametersException, PlatformException;

	protected void validateBindingNotExists(String bindingId, String instanceId)
			throws ServiceInstanceBindingExistsException {

		boolean bindCreation;
		boolean isBindingInProgress;

		if (bindingRepository.containsInternalBindingId(bindingId)) {
    		try {
    			bindCreation = jobRepository.getJobProgressByReferenceId(bindingId).getOperation().equals(JobProgress.BIND);
    			isBindingInProgress = jobRepository.getJobProgressByReferenceId(bindingId).getDescription().equals(JobProgress.IN_PROGRESS);
			 } catch (NoSuchElementException e) {
    			return;
			}
			if (bindCreation && !isBindingInProgress)
				throw new ServiceInstanceBindingExistsException(bindingId, instanceId);
		}
	}

	public ServiceInstance getServiceInstance(String instanceId) throws ServiceInstanceDoesNotExistException {
		ServiceInstance serviceInstance;
		try {
			serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);
			if (serviceInstance == null)
				throw new ServiceInstanceDoesNotExistException(instanceId);
		} catch(Exception e) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}
		return serviceInstance;
	}
}