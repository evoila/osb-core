package de.evoila.cf.broker.exception;

/**
 * @author Marco Di Martino
 */

public class ServiceInstanceBindingNotFoundException extends Exception {

    private static final long serialVersionUID = -7914853233472559899L;

    @Override
    public String getMessage() {
        return "Service Binding does not exist or binding operation is still in progress.";
    }
}
