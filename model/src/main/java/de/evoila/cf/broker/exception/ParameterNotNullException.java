package de.evoila.cf.broker.exception;

/**
 * @author Marco Di Martino.
 *
 */
import java.util.Map;

public class ParameterNotNullException extends Exception {

    private static final long serialVersionUID = 2L;

    private Map<String, Object> parameters;

    public ParameterNotNullException(Map<String, Object> parameters){

        this.parameters = parameters;
    }

    @Override
    public String getMessage() {
        return "Expected field parameters to be null"; 
    }
}
