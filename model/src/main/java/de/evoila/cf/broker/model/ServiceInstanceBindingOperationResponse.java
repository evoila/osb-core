package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

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

    public ServiceInstanceBindingOperationResponse() {
        this("");
    }

    public ServiceInstanceBindingOperationResponse(String operation) {
        this(operation, false);
    }

    public ServiceInstanceBindingOperationResponse(String operation, boolean async) {
        this.operation = operation;
        this.async = async;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        ServiceInstanceBindingOperationResponse that = (ServiceInstanceBindingOperationResponse) o;
        return Objects.equals(operation, that.operation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), operation);
    }

}
