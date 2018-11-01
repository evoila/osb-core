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
 * The response sent to the cloud controller when a bind request is successful.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceBindingResponse {

	private Map<String, Object> credentials;

	private String syslogDrainUrl;

	private String routeServiceUrl;

	private List<VolumeMount> volumeMounts;

	public ServiceInstanceBindingResponse(Map<String, Object> credentials, String syslogDrainUrl) {
		this.credentials = credentials;
		this.syslogDrainUrl = syslogDrainUrl;
	}

	public ServiceInstanceBindingResponse(String routeServiceUrl) {
		this.setRouteServiceUrl(routeServiceUrl);
	}

	public ServiceInstanceBindingResponse(ServiceInstanceBinding binding) {
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
}
