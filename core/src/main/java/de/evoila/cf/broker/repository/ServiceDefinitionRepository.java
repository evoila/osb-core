package de.evoila.cf.broker.repository;

import java.util.List;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceDefinition;

/**
 * @author Christian Brinker & Johannes Hiemer, evoila.
 *
 */
public interface ServiceDefinitionRepository {

    /**
     *
     * @return ServiceDefinition
     */
	List<ServiceDefinition> getServiceDefinition();

    /**
     *
     * @param serviceDefinitionId
     * @throws ServiceDefinitionDoesNotExistException
     */
	void validateServiceId(String serviceDefinitionId) throws ServiceDefinitionDoesNotExistException;

    /**
     *
     * @param planId
     * @return Plan
     * @throws ServiceDefinitionDoesNotExistException
     */
	Plan getPlan(String planId) throws ServiceDefinitionDoesNotExistException;

}