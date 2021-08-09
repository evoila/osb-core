package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;

import java.util.List;


/**
 * Handles the catalog of services made available by this 
 * broker.
 * 
 * @author sgreenberg@gopivotal.com
 */
public interface CatalogService {

	/**
	 * @return The catalog of services provided by this broker.
	 */
	Catalog getCatalog();

	/**
	 * @param serviceId  The id of the service in the catalog
	 * @return The service definition or null
	 * @throws ServiceDefinitionDoesNotExistException if the service definition does not exist.
	 */
	ServiceDefinition getServiceDefinition(String serviceId) throws ServiceDefinitionDoesNotExistException;

	/**
	 *
	 * @return list with ServiceIds without Hyphen
	 */
	List<String> getServiceIdsWithoutHyphen();
	
}
