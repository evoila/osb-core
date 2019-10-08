package de.evoila.cf.broker.exception;

/**
 * Thrown when a duplicate request to bind to a service instance is received.
 * 
 * @author sgreenberg@gopivotal.com
 */
public class ServiceInstanceBindingExistsException extends Exception {

	private static final long serialVersionUID = -914571358227517785L;

	private String bindingId;

	private String serviceInstanceId;


	/**
	 * Whether this exception was thrown because the binding request would create an identical binding.
	 */
	private boolean identicalBinding;

	public ServiceInstanceBindingExistsException(String bindingId, String serviceInstanceId, Boolean identicalBinding) {
		this.bindingId = bindingId;
		this.serviceInstanceId = serviceInstanceId;
		this.identicalBinding = identicalBinding;
	}

	@Override
	public String getMessage() {
		return "ServiceInstanceBinding already exists: serviceInstanceBinding.id = " + bindingId
				+ ", serviceInstance.id = " + serviceInstanceId;
	}

	/**
	 * Returns whether this exception was thrown because the binding request would create an identical binding.
	 * @return true if an identical service binding would have been created and false otherwise
	 */
	public boolean isIdenticalBinding() {
		return identicalBinding;
	}
}
