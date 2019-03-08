package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;

import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
public interface BackupCustomService {

    /**
     * Loads all the entitled Items with the default service credentials for a given Service Instance Id
     * E.g. in context of a database cluster: all databases entitled to the specific user.
     * @param serviceInstanceId
     * @return
     * @throws ServiceInstanceDoesNotExistException
     */
    Map<String, String> getItems(String serviceInstanceId) throws ServiceInstanceDoesNotExistException,
            ServiceDefinitionDoesNotExistException, ServiceBrokerException;

}
