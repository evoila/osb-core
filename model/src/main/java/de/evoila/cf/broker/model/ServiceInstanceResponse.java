package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Map;

/**
 * The response from the broker sent back to the cloud controller on a
 * successful service instance creation request
 *
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 * @author Christian Brinker, evoila.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceResponse {

	@JsonSerialize
	@JsonProperty("dashboard_url")
	private String dashboardUrl;

	@JsonIgnore
	private boolean isAsync;

	@JsonSerialize
	@JsonProperty("extension_apis")
	private List<Map<String, Object>> extension_apis;

	public ServiceInstanceResponse() {
	}

	public ServiceInstanceResponse(ServiceInstance serviceInstance, boolean isAsync, List<Map<String, Object>> extension_apis) {
		this.dashboardUrl = serviceInstance.getDashboardUrl();
		this.isAsync = true;
		this.extension_apis = extension_apis;
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

	public List<Map<String, Object>> getExtension_apis() {
		return extension_apis;
	}

	public void setExtension_apis(List<Map<String, Object>> extension_apis) {
		this.extension_apis = extension_apis;
	}
}
