package de.evoila.cf.broker.exception;

public class ServiceInstanceBindingBadRequestException extends Exception {

	private static final long serialVersionUID = 2169152862699358470L;
	
	private String bindingId;
	
    private String requestBody;
    
	public ServiceInstanceBindingBadRequestException(String bindingId, String requestBody) {
		this.bindingId = bindingId;
		this.requestBody = requestBody;
	}
    
	@Override
	public String getMessage() {
		return "ServiceInstanceBinding is a bad request: serviceInstanceBinding.id = " + bindingId
				 + ", requestBody = " + requestBody;
	}
}
