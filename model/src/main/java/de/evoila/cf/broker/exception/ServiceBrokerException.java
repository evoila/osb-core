package de.evoila.cf.broker.exception;

import java.util.Objects;

/**
 * General exception for underlying broker errors (like connectivity to the
 * service being brokered).
 * 
 * @author Johannes Hiemer.
 *
 */
public class ServiceBrokerException extends Exception {

	private static final long serialVersionUID = -5544859893499349135L;

	private String message;

	public ServiceBrokerException() { }

	public ServiceBrokerException(String message) {
		this.message = message;
	}

	public ServiceBrokerException(String message, Exception e) {
		super(message, e);
	}
	
	public ServiceBrokerException(Throwable t) {
		super(t);
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		ServiceBrokerException that = (ServiceBrokerException) o;
		return Objects.equals(message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message);
	}

}
