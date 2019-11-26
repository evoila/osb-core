package de.evoila.cf.broker.exception;

import java.util.Objects;

public class ServiceInstanceNotRetrievableException extends ServiceBrokerException  {

    private static final long serialVersionUID = 6277152862612888434L;

    private String message;

    public ServiceInstanceNotRetrievableException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        ServiceInstanceNotRetrievableException that = (ServiceInstanceNotRetrievableException) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message);
    }

}
