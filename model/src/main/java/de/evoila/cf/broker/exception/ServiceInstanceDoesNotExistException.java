package de.evoila.cf.broker.exception;

import java.util.Objects;

/**
 * Thrown when a request is received for an unknown ServiceInstance.
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public class ServiceInstanceDoesNotExistException extends Exception {
	
	private static final long serialVersionUID = -1879753092397657116L;

	private String serviceInstanceId;

	public ServiceInstanceDoesNotExistException(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}
	
	public String getMessage() {
		return "ServiceInstance does not exist: id = " + serviceInstanceId;
	}

	public String getError() {
		return "ServiceInstanceDoesNotExistException";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		ServiceInstanceDoesNotExistException that = (ServiceInstanceDoesNotExistException) o;
		return Objects.equals(serviceInstanceId, that.serviceInstanceId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(serviceInstanceId);
	}

}
