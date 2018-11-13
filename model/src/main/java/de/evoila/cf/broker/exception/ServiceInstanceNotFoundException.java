package de.evoila.cf.broker.exception;

/**
 * @author Marco Di Martino
 */

public class ServiceInstanceNotFoundException extends Exception {

    private static final long serialVersionUID = -5984853893472349837L;

    @Override
    public String getMessage() {
        return "Service Instance does not exists or an operation for this Instance is still in progress.";
    }
}

