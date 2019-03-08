package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.evoila.cf.broker.model.volume.VolumeMount;

import java.util.List;
import java.util.Map;

/**
 * @author sgreenberg@gopivotal.com, Johannes Hiemer.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceBindingResponse extends BaseServiceInstanceBindingResponse {

	private Map<String, Object> credentials;

	private String syslogDrainUrl;

	private String routeServiceUrl;

	private List<VolumeMount> volumeMounts;

	private String originatingUser;

	@JsonIgnore
	private boolean isAsync;

	public ServiceInstanceBindingResponse() {

	}

	public ServiceInstanceBindingResponse(Map<String, Object> credentials, String syslogDrainUrl) {
        this.async = false;
		this.credentials = credentials;
		this.syslogDrainUrl = syslogDrainUrl;
	}

	public ServiceInstanceBindingResponse(String routeServiceUrl) {
		this.setRouteServiceUrl(routeServiceUrl);
		this.async = false;
	}

	public ServiceInstanceBindingResponse(ServiceInstanceBinding binding) {
        this.async = false;
		this.credentials = binding.getCredentials();
		this.syslogDrainUrl = binding.getSyslogDrainUrl();
		if (binding.getVolumeMounts() != null && binding.getVolumeMounts().size() > 0) {
			this.volumeMounts = binding.getVolumeMounts();
		}
	}

	@JsonSerialize
	@JsonProperty("credentials")
	public Map<String, Object> getCredentials() {
		return this.credentials;
	}

	public void setCredentials(Map<String, Object> credentials) {
		this.credentials = credentials;
	}

	@JsonSerialize
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("syslog_drain_url")
	public String getSyslogDrainUrl() {
		return this.syslogDrainUrl;
	}

	public void setSyslogDrainUrl(String syslogDrainUrl) {
		this.syslogDrainUrl = syslogDrainUrl;
	}

	@JsonSerialize
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("route_service_url")
	public String getRouteServiceUrl() {
		return routeServiceUrl;
	}

	public void setRouteServiceUrl(String routeServiceUrl) {
		this.routeServiceUrl = routeServiceUrl;
	}

	@JsonSerialize
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("volume_mounts")
    public List<VolumeMount> getVolumeMounts() {
        return volumeMounts;
    }

    public void setVolumeMounts(List<VolumeMount> volumeMounts) {
        this.volumeMounts = volumeMounts;
    }

	@JsonSerialize
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("user_id")
	public String getOriginatingUser() {
		return originatingUser;
	}

	public void setOriginatingUser(String originatingUser) {
		this.originatingUser = originatingUser;
	}
}
