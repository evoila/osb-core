package de.evoila.cf.broker.exception;

public class ServiceBrokerFeatureIsNotSupportedException extends Exception {

	private static final long serialVersionUID = 8147070106781485530L;
	
	private String bindingId;
	
	private String instanceId;
	
	private String errorMessage;

	public ServiceBrokerFeatureIsNotSupportedException(String bindingId, String instanceId, String errorMessage) {
		this.bindingId = bindingId;
		this.instanceId = instanceId;
		this.errorMessage = errorMessage;
	}

	@Override
	public String getMessage() {
		return "ServiceBrokerFeature is not supported: serviceInstanceBinding.id = " + bindingId + ", serviceInstance.id= "
				+ instanceId + ", errorMessage = " + errorMessage;
	}

}
