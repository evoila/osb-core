package de.evoila.cf.broker.exception;

/**
 * @author Marco Di Martino.
 *
 */

import java.util.Map;

public class ParameterNotNullException extends Exception {

    private static final long serialVersionUID = 2L;

    private Map<String, String> parameters;

    public ParameterNotNullException(Map<String, String> parameters){

        this.parameters=parameters;
    }

    @Override
    public String getMessage() {
        return "Expected field parameters to be null"; 
    }
}
