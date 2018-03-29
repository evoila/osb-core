package de.evoila.cf.broker.model;

<<<<<<< HEAD
=======
import java.util.Map;

>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
<<<<<<< HEAD
import de.evoila.cf.broker.model.volume.VolumeMount;

import java.util.List;
import java.util.Map;
=======
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f

/**
 * The response sent to the cloud controller when a bind request is successful.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceBindingResponse {

	private Map<String, Object> credentials;

<<<<<<< HEAD
	private String syslogDrainUrl;

	private String routeServiceUrl;

    @JsonProperty("volume_mounts")
	private List<VolumeMount> volumeMounts;
=======
	private String syslogDrainUrl = "";

	private String routeServiceUrl = "";
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f

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
<<<<<<< HEAD
        if (binding.getVolumeMounts() != null && binding.getVolumeMounts().size() > 0)
            this.volumeMounts = binding.getVolumeMounts();
=======
		this.routeServiceUrl= "";
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
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
	@JsonProperty("syslog_drain_url")
	public String getSyslogDrainUrl() {
		return this.syslogDrainUrl;
	}

	public void setSyslogDrainUrl(String syslogDrainUrl) {
		this.syslogDrainUrl = syslogDrainUrl;
	}

	@JsonSerialize
	@JsonProperty("route_service_url")
	public String getRouteServiceUrl() {
		return routeServiceUrl;
	}

	public void setRouteServiceUrl(String routeServiceUrl) {
		this.routeServiceUrl = routeServiceUrl;
	}

<<<<<<< HEAD
    public List<VolumeMount> getVolumeMounts() {
        return volumeMounts;
    }

    public void setVolumeMounts(List<VolumeMount> volumeMounts) {
        this.volumeMounts = volumeMounts;
    }
=======
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
}
