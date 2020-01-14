package de.evoila.cf.broker.exception;

import java.util.Objects;

/**
 * Exception denoting an unknown ServiceDefintion
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public class ServiceDefinitionDoesNotExistException extends Exception {
	
	private static final long serialVersionUID = -62090827040416788L;

	private String serviceDefinitionId;
	
	public ServiceDefinitionDoesNotExistException(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}
	
	public String getMessage() {
		return "ServiceDefinition does not exist: id = " + serviceDefinitionId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		ServiceDefinitionDoesNotExistException that = (ServiceDefinitionDoesNotExistException) o;
		return Objects.equals(serviceDefinitionId, that.serviceDefinitionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(serviceDefinitionId);
	}

}
