package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.model.EndpointCredential;

import java.util.Map;

public interface BackupCustomService {

    /**
     * Loads the (admin/service) credentials for a given Service Instance Id
     * @param serviceInstanceId
     * @return
     * @throws ServiceInstanceDoesNotExistException
     */
    EndpointCredential getCredentials(String serviceInstanceId) throws ServiceInstanceDoesNotExistException;

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
