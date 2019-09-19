package de.evoila.cf.broker.exception;

/**
 * Thrown when a duplicate service instance creation request is received.
 * 
 * @author sgreenberg@gopivotal.com
 */
public class ServiceInstanceExistsException extends Exception {

	private String instanceId;

	private String serviceId;

	/**
	 * Whether this exception was thrown because the provision request would create an identical instance.
	 */
	private boolean identicalInstance;

	public ServiceInstanceExistsException(String instanceId, String serviceId) {
		this(instanceId, serviceId, false);
	}

	public ServiceInstanceExistsException(String instanceId, String serviceId, boolean identicalInstance) {
		this.instanceId = instanceId;
		this.serviceId = serviceId;
		this.identicalInstance = identicalInstance;
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
}
