/**
 * 
 */
package de.evoila.cf.broker.service;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import de.evoila.cf.broker.exception.*;
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
            ServiceBrokerException, ServiceDefinitionDoesNotExistException, ProcessingException, InvalidParametersException;

    void updateServiceInstance(String serviceInstanceId, ServiceInstanceRequest serviceInstanceRequest) throws ServiceBrokerException,
            ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, InvalidParametersException;

	void deleteServiceInstance(String instanceId) throws ServiceBrokerException, ServiceDefinitionDoesNotExistException,
            ServiceInstanceDoesNotExistException;
}
