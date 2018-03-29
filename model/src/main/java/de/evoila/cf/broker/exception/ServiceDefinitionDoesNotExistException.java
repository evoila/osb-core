package de.evoila.cf.broker.exception;

/**
<<<<<<< HEAD
 * Exception denoting an unknown ServiceDefintion
=======
 * Exception denoting an unknown ServiceDefinition
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public class ServiceDefinitionDoesNotExistException extends Exception {
	
	private static final long serialVersionUID = -62090827040416788L;
	private String serviceDefinitionId;
	
	public ServiceDefinitionDoesNotExistException(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}
	
	public String getMessage() {
		return "ServiceDefinition does not exist: id = " + serviceDefinitionId;
	}
	
}