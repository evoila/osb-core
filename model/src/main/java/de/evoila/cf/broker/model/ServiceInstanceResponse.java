package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The response from the broker sent back to the cloud controller on a
 * successful service instance creation request
 *
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 * @author Christian Brinker, evoila.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceResponse {

	@JsonSerialize
	@JsonProperty("dashboard_url")
	private String dashboardUrl;

	@JsonIgnore
	private boolean isAsync;

	@JsonSerialize
	@JsonProperty("extension_apis")
	private List<Map<String, Object>> extensionApis;

	@JsonSerialize
	@JsonProperty("service_id")
	private String serviceId;

	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;

	@JsonSerialize
	@JsonProperty("parameters")
	private Map<String, Object> parameters;

	public ServiceInstanceResponse() {
	}

	public ServiceInstanceResponse(ServiceInstance serviceInstance, boolean isAsync, List<Map<String, Object>> extensionApis) {
		this.dashboardUrl = serviceInstance.getDashboardUrl();
		this.isAsync = true;
		this.extensionApis = extensionApis;
		this.parameters = serviceInstance.getParameters();
		this.planId = serviceInstance.getPlanId();
		this.serviceId = serviceInstance.getServiceDefinitionId();
	}

	public ServiceInstanceResponse(ServiceInstance serviceInstance){
		this.serviceId = serviceInstance.getServiceDefinitionId();
		this.planId = serviceInstance.getPlanId();
		this.parameters = serviceInstance.getParameters();
		this.dashboardUrl = serviceInstance.getDashboardUrl();

	}

	public ServiceInstanceResponse(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	public void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	@JsonIgnore
	public boolean isAsync() {
		return isAsync;
	}

	public List<Map<String, Object>> getExtensionApis() {
		return extensionApis;
	}

	public void setExtensionApis(List<Map<String, Object>> extensionApis) {
		this.extensionApis = extensionApis;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		ServiceInstanceResponse that = (ServiceInstanceResponse) o;
		return isAsync == that.isAsync &&
			   Objects.equals(dashboardUrl, that.dashboardUrl) &&
			   Objects.equals(extensionApis, that.extensionApis) &&
			   serviceId.equals(that.serviceId) &&
			   planId.equals(that.planId) &&
			   Objects.equals(parameters, that.parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dashboardUrl, isAsync, extensionApis, serviceId, planId, parameters);
	}

}
