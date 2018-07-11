package de.evoila.cf.broker.exception;

import java.util.Map;

public class InvalidParametersException extends Exception{
    private static final long serialVersionUID = 2L;

    private Map<String, Object> parameters;

    private String errorMesssage;

    public InvalidParametersException(String errorMessage){
        this.errorMesssage = errorMessage;
    }

    public InvalidParametersException(Map<String, Object> parameters){

        this.parameters = parameters;
    }

    @Override
    public String getMessage()
    {
        if (errorMesssage == null){
            return "The specified parameters are invalid ";
        }else{
            return this.errorMesssage;
        }
    }
}

