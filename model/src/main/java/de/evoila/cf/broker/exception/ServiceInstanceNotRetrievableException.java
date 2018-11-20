package de.evoila.cf.broker.exception;

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
}
