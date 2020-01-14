package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author sgreenberg@gopivotal.com, Johannes Hiemer.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstance implements BaseEntity<String> {

	@JsonSerialize
	@JsonProperty("service_instance_id")
	private String id;

	@JsonSerialize
	@JsonProperty("service_id")
	private String serviceDefinitionId;

	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;

	@JsonSerialize
	@JsonProperty("organization_guid")
	private String organizationGuid;

	@JsonSerialize
	@JsonProperty("space_guid")
	private String spaceGuid;

	@JsonIgnore
	private String dashboardUrl;

	@JsonSerialize
	@JsonProperty("parameters")
	private Map<String, Object> parameters = new HashMap<>();

	@JsonSerialize
	@JsonProperty("internal_id")
	private String internalId;

	@JsonSerialize
	@JsonProperty("hosts")
	private List<ServerAddress> hosts = new ArrayList<>();
	
	@JsonSerialize
	@JsonProperty("context")
	private Context context;

	@JsonSerialize
	@JsonProperty("floatingIp_id")
	private String floatingIpId;

    @JsonIgnore
    private String username;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String usergroup;

    @JsonSerialize
	@JsonProperty("allow_context_updates")
    private boolean allowContextUpdates;

	@SuppressWarnings("unused")
	private ServiceInstance() {}

	public ServiceInstance(String id, String serviceDefinitionId, String planId, String organizationGuid,
			String spaceGuid, Map<String, Object> parameters, String dashboardUrl) {
		initialize(id, serviceDefinitionId, planId, organizationGuid, spaceGuid, parameters);
		setDashboardUrl(dashboardUrl);
	}

	private void initialize(String id, String serviceDefinitionId, String planId, String organizationGuid,
			String spaceGuid, Map<String, Object> parameters) {
		setId(id);
		setServiceDefinitionId(serviceDefinitionId);
		setPlanId(planId);
		setOrganizationGuid(organizationGuid);
		setSpaceGuid(spaceGuid);
		if (parameters != null)
			setParameters(parameters);
	}

	public ServiceInstance(String serviceInstanceId, String serviceDefinitionId, String planId, String organizationGuid,
			String spaceGuid, Map<String, Object> parameters, String dashboardUrl, String internalId) {
		initialize(serviceInstanceId, serviceDefinitionId, planId, organizationGuid, spaceGuid, parameters);
		setInternalId(internalId);
		setDashboardUrl(dashboardUrl);
	}

	public ServiceInstance(ServiceInstance serviceInstance, String dashboardUrl, String internalId) {
		initialize(serviceInstance.id, serviceInstance.serviceDefinitionId, serviceInstance.planId,
				serviceInstance.organizationGuid, serviceInstance.spaceGuid, serviceInstance.parameters);
		this.setHosts(serviceInstance.getHosts());
		setInternalId(internalId);
		setDashboardUrl(dashboardUrl);
	}

	public ServiceInstance(ServiceInstance serviceInstance, String dashboardUrl, String internalId,
			List<ServerAddress> hosts) {
		initialize(serviceInstance.id, serviceInstance.serviceDefinitionId, serviceInstance.planId,
				serviceInstance.organizationGuid, serviceInstance.spaceGuid, serviceInstance.parameters);
		setInternalId(internalId);
		setDashboardUrl(dashboardUrl);
		setHosts(hosts);
	}

	public ServiceInstance(String serviceInstanceId, String serviceDefinitionId, String planId, String organizationGuid,
			String spaceGuid, Map<String, Object> parameters, Context context) {
		initialize(serviceInstanceId, serviceDefinitionId, planId, organizationGuid, spaceGuid, parameters);
		if(context != null)
			setContext(context);
	}

    public ServiceInstance(ServiceInstance serviceInstance, String internalId) {
        initialize(serviceInstance.id, serviceInstance.serviceDefinitionId, serviceInstance.planId,
                serviceInstance.organizationGuid, serviceInstance.spaceGuid, serviceInstance.parameters);
        setInternalId(internalId);
    }

	@Override
	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	private void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}
	
	public void updatePlanId(String planId){
		this.setPlanId(planId);
	}

	public String getOrganizationGuid() {
		return organizationGuid;
	}

	private void setOrganizationGuid(String organizationGuid) {
		this.organizationGuid = organizationGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	private void setSpaceGuid(String spaceGuid) {
		this.spaceGuid = spaceGuid;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	public void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = new HashMap<>(parameters);
	}

	public String getInternalId() {
		return internalId;
	}

	private void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public List<ServerAddress> getHosts() {
		return hosts;
	}

	public void setHosts(List<ServerAddress> hosts) {
		this.hosts = hosts;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getUsergroup() { return usergroup; }

    public void setUsergroup(String usergroup) { this.usergroup = usergroup; }

	public String getFloatingIpId() { return floatingIpId; }

	public void setFloatingIpId(String floatingIpId) { this.floatingIpId = floatingIpId; }

	public boolean isAllowContextUpdates() {
		return allowContextUpdates;
	}

	public void setAllowContextUpdates(boolean allowContextUpdates) {
		this.allowContextUpdates = allowContextUpdates;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		ServiceInstance that = (ServiceInstance) o;
		return allowContextUpdates == that.allowContextUpdates &&
			   Objects.equals(id, that.id) &&
			   Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
			   Objects.equals(planId, that.planId) &&
			   Objects.equals(organizationGuid, that.organizationGuid) &&
			   Objects.equals(spaceGuid, that.spaceGuid) &&
			   Objects.equals(dashboardUrl, that.dashboardUrl) &&
			   Objects.equals(parameters, that.parameters) &&
			   Objects.equals(internalId, that.internalId) &&
			   Objects.equals(hosts, that.hosts) &&
			   Objects.equals(context, that.context) &&
			   Objects.equals(floatingIpId, that.floatingIpId) &&
			   Objects.equals(username, that.username) &&
			   Objects.equals(password, that.password) &&
			   Objects.equals(usergroup, that.usergroup);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, serviceDefinitionId, planId, organizationGuid, spaceGuid, dashboardUrl, parameters, internalId, hosts, context, floatingIpId, username, password, usergroup, allowContextUpdates);
	}

}
