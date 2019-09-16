package de.evoila.cf.broker.exception;

import de.evoila.cf.broker.model.catalog.MaintenanceInfo;
import org.springframework.http.HttpStatus;

public class MaintenanceInfoVersionsDontMatchException extends ServiceBrokerErrorException {

    private static final String INFO_IS_NULL_MESSAGE= "none";
    private static final String OSB_ERROR_DESCRIPTION_CHANGED = "The maintenance information for the requested Service Plan has changed.";
    private static final String OSB_ERROR_DESCRIPTION_EMTPY = "There exists no maintenance information for the requested Service Plan, but the request contains it.";

    private String requestVersion;
    private String actualVersion;
    private String description;

    public MaintenanceInfoVersionsDontMatchException(String requestVersion, String actualVersion) {
        this(requestVersion, actualVersion, OSB_ERROR_DESCRIPTION_CHANGED);
    }

    public MaintenanceInfoVersionsDontMatchException(MaintenanceInfo requestInfo, MaintenanceInfo planInfo) {
        this(
                requestInfo == null ? INFO_IS_NULL_MESSAGE : requestInfo.getVersion(),
                planInfo == null ? INFO_IS_NULL_MESSAGE : planInfo.getVersion(),
                requestInfo != null && planInfo == null ? OSB_ERROR_DESCRIPTION_EMTPY : OSB_ERROR_DESCRIPTION_CHANGED
        );
    }

    public MaintenanceInfoVersionsDontMatchException(String requestVersion, String actualVersion, String description) {
        this.requestVersion = requestVersion;
        this.actualVersion = actualVersion;
        this.description = description;
    }

    @Override
    public String getMessage() {
        return "The Maintenance Versions did not match. Version in Request: " + requestVersion +
                " - Supported Version: " + actualVersion;
    }

    public String getError() {
        return "MaintenanceInfoConflict";
    }

    public String getDescription() {
        return description;
    }
}
