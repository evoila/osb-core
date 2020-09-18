package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceDefinitionPlanDoesNotExistException;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;

import java.util.List;

/**
 * @author Christian Brinker, Johannes Hiemer.
 */
public interface ServiceDefinitionRepository {

    List<ServiceDefinition> getServiceDefinitions();

    ServiceDefinition getServiceDefinition(String id) throws ServiceDefinitionDoesNotExistException;

	void validateServiceId(String serviceDefinitionId) throws ServiceDefinitionDoesNotExistException;

    Plan getPlan(String serviceId, String planId) throws ServiceDefinitionDoesNotExistException, ServiceDefinitionPlanDoesNotExistException;

}