package de.evoila.cf.broker.exception;

public class BadHeaderException extends Exception {

    private static final long serialVersionUID = 2L;

    private String api_version;

    public BadHeaderException(String api_version){
        this.api_version=api_version;
    }

    @Override
    public String getMessage() {
        if (api_version == null) {
            return "Requests to Service Broker must contain header that declares API-version";
        } else {
            return "Expected API-version: 2.13, but found API-version:" + api_version;
        }
    }
}
