/**
 * 
 */
package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.JobProgressResponse;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.ServiceInstanceResponse;

import java.util.List;
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

	ServiceInstanceResponse createServiceInstance(String serviceInstanceId, ServiceInstanceRequest serviceInstanceRequest00, List<Map<String, Object>> exentension_apis) throws ServiceInstanceExistsException,
            ServiceBrokerException, ServiceDefinitionDoesNotExistException;

    void updateServiceInstance(String serviceInstanceId, ServiceInstanceRequest serviceInstanceRequest) throws ServiceBrokerException,
            ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException;

	void deleteServiceInstance(String instanceId) throws ServiceBrokerException, ServiceDefinitionDoesNotExistException,
            ServiceInstanceDoesNotExistException;

}
