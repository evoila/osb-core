/**
 * 
 */
package de.evoila.cf.broker.service;

import java.util.Map;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.ServiceInstanceResponse;
import de.evoila.cf.broker.model.JobProgressResponse;

/**
 * @author Christian Brinker, evoila.
 *
 */
public abstract interface DeploymentService {
	

	JobProgressResponse getLastOperation(String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException, ServiceBrokerException;

	public ServiceInstanceResponse createServiceInstance(String serviceInstanceId, String serviceDefinitionId,
			String planId, String organizationGuid, String spaceGuid, Map<String, String> parameters,
			Map<String, String> context)
					throws ServiceInstanceExistsException, ServiceBrokerException,
					ServiceDefinitionDoesNotExistException;

	public void deleteServiceInstance(String instanceId) throws ServiceBrokerException, ServiceInstanceDoesNotExistException;

}
