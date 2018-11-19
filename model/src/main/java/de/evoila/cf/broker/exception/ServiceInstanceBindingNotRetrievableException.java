package de.evoila.cf.broker.exception;

public class ServiceInstanceBindingNotRetrievableException extends ServiceBrokerException {

    private static final long serialVersionUID = 2109152234692568475L;

    private String message;

    public ServiceInstanceBindingNotRetrievableException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
