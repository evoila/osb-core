package de.evoila.cf.broker.exception;

import de.evoila.cf.broker.model.ServiceInstanceOperationResponse;

/**
 * Thrown when a duplicate service instance creation request is received.
 * 
 * @author sgreenberg@gopivotal.com
 */
public class ServiceInstanceExistsException extends Exception {

	private String instanceId;

	private String serviceId;

    /**
     *  Whether this exception was thrown because the provision request would create an identical instance,
     *  we want te return information about the service instance.
     */
    private ServiceInstanceOperationResponse response;

	/**
	 * Whether this exception was thrown because the provision request would create an identical instance.
	 */
	private boolean identicalInstance;

	public ServiceInstanceExistsException(String instanceId, String serviceId) {
		this(instanceId, serviceId, false, null);
	}

	public ServiceInstanceExistsException(String instanceId, String serviceId, boolean identicalInstance,
                                          ServiceInstanceOperationResponse response) {
		this.instanceId = instanceId;
		this.serviceId = serviceId;
		this.identicalInstance = identicalInstance;
        this.response = response;
    }

	@Override
	public String getMessage() {
		return "ServiceInstance with the given id already exists: ServiceInstance.id = " + instanceId
				+ ", Service.id = " + serviceId;
	}

	/**
	 * Returns whether this exception was thrown because the provision request would create an identical instance.
	 * @return true if an identical service instance would have been created and false otherwise
	 */
	public boolean isIdenticalInstance() {
		return identicalInstance;
	}

    /**
     * Returns the ServiceInstanceOperationResponse, that displays the information of the Service Instance that would be
     * identical with the provision request.
     * @return the ServiceInstanceOperationResponse that was provided, when creating this exception.
     */
    public ServiceInstanceOperationResponse getResponse() {
        return response;
    }
}
