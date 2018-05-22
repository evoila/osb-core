/**
 * 
 */
package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.JobProgressResponse;
import de.evoila.cf.broker.model.ServiceInstanceResponse;

import java.util.Map;

/**
 * @author Christian Brinker && Johannes Hiemer, evoila.
 *
 */
public interface DeploymentService {

	// List<ServiceInstance> getAllServiceInstances();

	// ServiceInstance getServiceInstance(String id);

	JobProgressResponse getLastOperation(String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException, ServiceBrokerException;

	ServiceInstanceResponse createServiceInstance(String serviceInstanceId, String serviceDefinitionId,
			String planId, String organizationGuid, String spaceGuid, Map<String, Object> parameters,
			Map<String, String> context)
					throws ServiceInstanceExistsException, ServiceBrokerException,
					ServiceDefinitionDoesNotExistException;

	void deleteServiceInstance(String instanceId) throws ServiceBrokerException, ServiceDefinitionDoesNotExistException,
            ServiceInstanceDoesNotExistException;

	void updateServiceInstance(String instanceId, String planId) throws ServiceBrokerException, ServiceInstanceDoesNotExistException;
}
