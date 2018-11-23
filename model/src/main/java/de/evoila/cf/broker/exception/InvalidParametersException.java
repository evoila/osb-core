package de.evoila.cf.broker.exception;

public class InvalidParametersException extends Exception{

    private static final long serialVersionUID = 858570104362828780L;

    private String errorMessage;

    public InvalidParametersException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        if (errorMessage == null) {
            return "The specified parameters are invalid";
        } else {
            return this.errorMessage;
        }
    }
}

