package de.evoila.cf.broker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * @author Marco Di Martino, Marius Berger
 */

public class ConcurrencyErrorException extends ServiceBrokerErrorException {

    /**
     * Field to identify the object's kind in a readable way.
     */
    private String objectName;

    public ConcurrencyErrorException() {
        this(null);
    }

    /**
     * Constructor of this exception with a String parameter to get the name of the blocked object.
     * This name is supposed to represent a readable version of the class / component name.
     * For example ServiceInstance should get the name "Service Instance" and ServiceInstanceBinding the name "Service Binding"
     * @param objectName readable version of the objects name
     */
    public ConcurrencyErrorException(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public String getError() {
        return "ConcurrencyError";
    }

    @Override
    public String getDescription() {
        return "Another operation" + (StringUtils.isEmpty(objectName) ? "" : " for this " + objectName) + " is in progress.";
    }
}