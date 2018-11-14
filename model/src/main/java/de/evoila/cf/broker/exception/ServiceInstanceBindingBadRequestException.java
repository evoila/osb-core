package de.evoila.cf.broker.exception;

public class ServiceInstanceBindingBadRequestException extends ServiceBrokerException {

	private static final long serialVersionUID = 2169152862699358470L;
	
	private String bindingId;
	
    private String requestBody;
    
	public ServiceInstanceBindingBadRequestException(String bindingId, String requestBody) {
		this.bindingId = bindingId;
		this.requestBody = requestBody;
	}

	public ServiceInstanceBindingBadRequestException(String bindingId) {
		this.bindingId = bindingId;
	}

	@Override
	public String getMessage() {
		if (requestBody != null){
		return "ServiceInstanceBinding is a bad request: serviceInstanceBinding.id = " + bindingId
				 + ", requestBody = " + requestBody;
		}else
			return "Such binding request can not be performed by the Platform: id = " + bindingId;
		}
}
