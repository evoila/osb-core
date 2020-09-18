package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;

/**
 * @author Christian Brinker, Johannes Hiemer.
 */
public interface DeploymentService {

	JobProgressResponse getLastOperationByReferenceId(String referenceId)
			throws ServiceInstanceDoesNotExistException;

	JobProgressResponse getLastOperationById(String referenceId, String jobProgressId) throws ServiceInstanceDoesNotExistException;

    ServiceInstanceOperationResponse createServiceInstance(String serviceInstanceId, ServiceInstanceRequest serviceInstanceRequest)
            throws ServiceInstanceExistsException, ServiceBrokerException, ServiceDefinitionDoesNotExistException, ServiceDefinitionPlanDoesNotExistException;

    ServiceInstanceOperationResponse updateServiceInstance(String serviceInstanceId, ServiceInstanceUpdateRequest serviceInstanceUpdateRequest)
            throws ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, ServiceDefinitionPlanDoesNotExistException;

	ServiceInstanceOperationResponse updateServiceInstanceContext(String serviceInstanceId, ServiceInstanceUpdateRequest serviceInstanceUpdateRequest)
			throws ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException;

	ServiceInstanceOperationResponse deleteServiceInstance(String instanceId) throws ServiceBrokerException, ServiceDefinitionDoesNotExistException,
			ServiceInstanceDoesNotExistException, ServiceDefinitionPlanDoesNotExistException;

	ServiceInstance fetchServiceInstance(String instanceId) throws UnsupportedOperationException, ServiceBrokerException,
            ConcurrencyErrorException, ServiceInstanceNotFoundException;

}
