package de.evoila.cf.broker.exception;

import org.springframework.util.StringUtils;

/**
 * @author Marco Di Martino
 */

public class ServiceInstanceNotFoundException extends Exception {

    private static final long serialVersionUID = -5984853893472349837L;

    public String getError() {
        return "ServiceInstanceNotFound";
    }

    public ServiceInstanceNotFoundException() {
        this("");
    }

    public ServiceInstanceNotFoundException(String serviceInstanceId) {
        super("Service Instance "
                + (StringUtils.isEmpty(serviceInstanceId)? "" : serviceInstanceId + " ")
                + "does not exists or an operation for this Instance is still in progress.");
    }

}

