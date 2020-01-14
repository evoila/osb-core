/**
 * 
 */
package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author Christian Brinker, Johannes Hiemer.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceInstanceOperationResponse {

	private String operation;

	@JsonProperty("dashboard_url")
	private String dashboardUrl;

	@JsonIgnore
    private boolean async;

	public ServiceInstanceOperationResponse() {}

    public ServiceInstanceOperationResponse(String operation) {
        this(operation, "");
    }

    public ServiceInstanceOperationResponse(String operation, String dashboardUrl) {
        this(operation, dashboardUrl, false);
    }

    public ServiceInstanceOperationResponse(String operation, String dashboardUrl, boolean isAsync) {
	    this.operation = operation;
	    this.dashboardUrl = dashboardUrl;
	    this.async = isAsync;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ServiceInstanceOperationResponse that = (ServiceInstanceOperationResponse) o;
        return async == that.async &&
               Objects.equals(operation, that.operation) &&
               Objects.equals(dashboardUrl, that.dashboardUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, dashboardUrl, async);
    }

}
