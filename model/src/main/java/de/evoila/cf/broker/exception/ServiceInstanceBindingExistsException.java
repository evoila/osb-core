package de.evoila.cf.broker.exception;

import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;

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
     * When this exception was thrown because the binding request would create an identical binding,
     * we want to return information about the service binding.
     */
    private ServiceInstanceBindingResponse response;

	/**
	 * Whether this exception was thrown because the binding request would create an identical binding.
	 */
	private boolean identicalBinding;

    public ServiceInstanceBindingExistsException(String bindingId, String serviceInstanceId) {
        this(bindingId, serviceInstanceId, false, null);
    }

    public ServiceInstanceBindingExistsException(String bindingId, String serviceInstanceId, Boolean identicalBinding,
                                                 ServiceInstanceBindingResponse response) {
        this.bindingId = bindingId;
        this.serviceInstanceId = serviceInstanceId;
        this.identicalBinding = identicalBinding;
        this.response = response;
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

    /**
     * Returns the ServiceInstanceBindingOperationResponse, that displays the information of the Service Binding that would be
     * identical with the binding request.
     *
     * @return the ServiceInstanceBindingOperationResponse that was provided, when creating this exception.
     */
    public ServiceInstanceBindingResponse getResponse() {
        return response;
    }
}
