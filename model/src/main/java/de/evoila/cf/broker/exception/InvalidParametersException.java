package de.evoila.cf.broker.exception;

import java.util.Map;

public class InvalidParametersException extends Exception{
    private static final long serialVersionUID = 2L;

    private Map<String, Object> parameters;

    private String errorMessage;

    public InvalidParametersException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public InvalidParametersException(Map<String, Object> parameters){
        this.parameters = parameters;
    }

    @Override
    public String getMessage()
    {
        if (errorMessage == null){
            return "The specified parameters are invalid";
        }else{
            return this.errorMessage;
        }
    }
}

