package de.evoila.cf.broker.model;

import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.volume.VolumeMount;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A binding to a service instance
 *
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
public class ServiceInstanceBinding implements BaseEntity<String> {

	private String id;

	private String serviceInstanceId;

	private Map<String, Object> credentials = new HashMap<String, Object>();

	private String syslogDrainUrl;

	private String appGuid;

    private Map<String, Object> parameters = new HashMap<>();

	private List<VolumeMount> volumeMounts;

	public ServiceInstanceBinding() {
	}

    public ServiceInstanceBinding(String id, String serviceInstanceId, Map<String, Object> credentials) {
		this(id, serviceInstanceId, credentials, null);
    }

	public ServiceInstanceBinding(String id, String serviceInstanceId, Map<String, Object> credentials,
								  String syslogDrainUrl) {
		this.id = id;
		this.serviceInstanceId = serviceInstanceId;
		setCredentials(credentials);
		this.syslogDrainUrl = syslogDrainUrl;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public Map<String, Object> getCredentials() {
		return credentials;
	}

	private void setCredentials(Map<String, Object> credentials) {
	    this.credentials = credentials;
	}

	public String getSyslogDrainUrl() {
		return syslogDrainUrl;
	}

	public String getAppGuid() {
		return appGuid;
	}

	public void setAppGuid(String appGuid) { this.appGuid = appGuid; }

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public List<VolumeMount> getVolumeMounts() {
		return volumeMounts;
	}

	public void setVolumeMounts(List<VolumeMount> volumeMounts) {
		this.volumeMounts = volumeMounts;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		ServiceInstanceBinding that = (ServiceInstanceBinding) o;
		return Objects.equals(id, that.id) &&
			   Objects.equals(serviceInstanceId, that.serviceInstanceId) &&
			   Objects.equals(credentials, that.credentials) &&
			   Objects.equals(syslogDrainUrl, that.syslogDrainUrl) &&
			   Objects.equals(appGuid, that.appGuid) &&
			   Objects.equals(parameters, that.parameters) &&
			   Objects.equals(volumeMounts, that.volumeMounts);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, serviceInstanceId, credentials, syslogDrainUrl, appGuid, parameters, volumeMounts);
	}

}
