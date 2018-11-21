package de.evoila.cf.broker.exception;

import java.io.Serializable;

/**
 * @author Marco Di Martino
 */

public class ConcurrencyErrorException extends Exception {

    private static final long serialVersionUID = 42L;

    @Override
    public String getMessage() {
        return "Service Instance is being updated and therefore cannot be fetched.";
    }
}