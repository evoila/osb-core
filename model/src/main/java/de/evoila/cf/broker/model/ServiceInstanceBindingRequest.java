package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

/**
 * Request sent from the cloud controller to bind to a service instance.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 *
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceBindingRequest {

	@NotEmpty
	@JsonSerialize
	@JsonProperty("service_id")
	private String serviceDefinitionId;

	@NotEmpty
	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;

	@Deprecated
	@JsonSerialize
	@JsonProperty("app_guid")
	private String appGuid;

	@JsonSerialize
	@JsonProperty("parameters")
	private Map<String, Object> parameters = new HashMap<>();

	@JsonSerialize
	@JsonProperty("bind_resource")
	private BindResource bindResource;

	public ServiceInstanceBindingRequest() {}

	public ServiceInstanceBindingRequest(String serviceDefinitionId, String planId, String appGuid,
                                         BindResource bindResource) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.appGuid = appGuid;
		this.bindResource = bindResource;
	}

	public ServiceInstanceBindingRequest(String serviceDefinitionId, String planId) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getAppGuid() {
		return appGuid;
	}

	public void setAppGuid(String appGuid) {
		this.appGuid = appGuid;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public BindResource getBindResource() {
		return bindResource;
	}

	public void setBindResource(BindResource bindResource) {
		this.bindResource = bindResource;
	}

}
