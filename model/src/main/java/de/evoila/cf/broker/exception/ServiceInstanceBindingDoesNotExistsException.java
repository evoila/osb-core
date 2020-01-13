/**
 * 
 */
package de.evoila.cf.broker.exception;

import java.util.Objects;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class ServiceInstanceBindingDoesNotExistsException extends Exception {

	private static final long serialVersionUID = -1879753092397657116L;

	private String bindingId;

	public ServiceInstanceBindingDoesNotExistsException(String bindingId) {
		this.bindingId = bindingId;
	}

	@Override
	public String getMessage() {
		return "ServiceInstanceBinding does not exist: id = " + bindingId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		ServiceInstanceBindingDoesNotExistsException that = (ServiceInstanceBindingDoesNotExistsException) o;
		return Objects.equals(bindingId, that.bindingId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bindingId);
	}

}

