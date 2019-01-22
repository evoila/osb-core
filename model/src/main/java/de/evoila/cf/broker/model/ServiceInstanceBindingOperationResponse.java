package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Johannes Hiemer.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceInstanceBindingOperationResponse extends BaseServiceInstanceBindingResponse {

    private String operation;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public ServiceInstanceBindingOperationResponse() {}

    public ServiceInstanceBindingOperationResponse(String operation) {
        this.operation = operation;
        this.async = true;
    }

}
