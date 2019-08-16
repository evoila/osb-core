package de.evoila.cf.broker.exception;

public class MaintenanceInfoVersionsDontMatchException extends Throwable {

    private String requestVersion;
    private String actualVersion;

    public MaintenanceInfoVersionsDontMatchException(String requestVersion, String actualVersion) {
        this.requestVersion = requestVersion;
        this.actualVersion = actualVersion;
    }

    @Override
    public String getMessage() {
        return "The Maintenance Versions did not match. Version in Request: " + requestVersion +
                " Supported Version: " + actualVersion;
    }
}
