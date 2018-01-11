package de.evoila.cf.broker.exception;

import org.springframework.http.HttpStatus;

/**
 * 
 * @author Marius Berger
 *
 */
public class ServiceInstanceBindingException extends Exception{

	private static final long serialVersionUID = 8243647155537277129L;

	private String instanceId;
	
	private String bindingId;
	
	private HttpStatus httpStatus;
	
	private String httpBody;
	
	public ServiceInstanceBindingException(String instanceId, String bindingId, HttpStatus httpStatus, String httpBody) {
		this.instanceId = instanceId;
		this.bindingId = bindingId;
		this.httpStatus = httpStatus;
		this.httpBody = httpBody;
	}
	
	@Override
	public String getMessage() {
		return "Binding resulted in " + httpStatus.value() + ", instanceId = " + instanceId + ", bindingId = " + bindingId + ", httpBody = '" + httpBody + "'";
	}
}
