package de.evoila.cf.broker.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by reneschollmeyer, evoila on 22.11.17.
 */
public class ServiceInstanceBindingException extends Exception {

    private String instanceId;
    private String bindingId;
    private HttpStatus httpStatus;
    private String errorMessage;

    public ServiceInstanceBindingException(String instanceId, String bindingId,
                                           HttpStatus httpStatus, String errorMessage) {
        this.instanceId = instanceId;
        this.bindingId = bindingId;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    public ServiceInstanceBindingException(String bindingId, HttpStatus httpStatus, String errorMessage) {
        this.bindingId = bindingId;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        if(instanceId.isEmpty()) {
            return "Error updating configuration for binding = " + bindingId
                    +". Request resulted in " + httpStatus  + ", error message: " + errorMessage;
        } else {
            return "Error binding ServiceInstance = " + instanceId + " and BindingId = " + bindingId
                    + ". Binding resulted in " + httpStatus + ", error message: " + errorMessage;
        }
    }
}
