package de.evoila.cf.broker.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParameterNotNullException extends Exception {

    private static final long serialVersionUID = 2L;

    private Map<String, String> parameters;

    public ParameterNotNullException(Map<String, String> parameters){

        this.parameters=parameters;
    }

    @Override
    public String getMessage() {
        return "Expected field parameters to be null"; //, but found values :" + param;
    }
}
