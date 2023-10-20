package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.util.ObjectUtils;

import jakarta.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

	/**
	 * Returns the app guid of the service instance binding request.
	 * Due to the deprecated status of the field appGuid in this class, the Getter will try to access the
	 * appGuid of its {@linkplain #bindResource} object. If the BindResource is null or its appGuid value is empty,
	 * the field of this calls will be returned.
	 * @return Id of the referenced application
	 */
	@Deprecated
	public String getAppGuid() {
		if (bindResource != null && !ObjectUtils.isEmpty(bindResource.getAppGuid())) {
			return bindResource.getAppGuid();
		}
		return appGuid;
	}

	/**
	 * Sets the app guid of the service instance binding request.
	 * Due to the deprecated status of the field appGuid in this class, the Setter will also try to set the
	 * appGuid of its {@linkplain #bindResource} object. This would cause calls of the {@linkplain #getAppGuid()}
	 * to return the appGuid field of the {@linkplain #bindResource} object instead of the deprecated appGuid field
	 * of this class.
	 * @param appGuid String with the Id of the referenced application to set to
	 */
	@Deprecated
	public void setAppGuid(String appGuid) {
		this.appGuid = appGuid;
		if (bindResource != null) {
			bindResource.setAppGuid(appGuid);
		}
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

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		ServiceInstanceBindingRequest that = (ServiceInstanceBindingRequest) o;
		return Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
			   Objects.equals(planId, that.planId) &&
			   Objects.equals(appGuid, that.appGuid) &&
			   Objects.equals(parameters, that.parameters) &&
			   Objects.equals(bindResource, that.bindResource);
	}

	@Override
	public int hashCode() {
		return Objects.hash(serviceDefinitionId, planId, appGuid, parameters, bindResource);
	}

}
