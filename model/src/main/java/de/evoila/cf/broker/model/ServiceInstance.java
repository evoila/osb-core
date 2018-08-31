package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An instance of a ServiceDefinition.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 *
 */
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
	private Map<String, String> context;

	@JsonSerialize
	@JsonProperty("floatingIp_id")
	private String floatingIpId;

	/**
	 * Use users instead
	 */
    @JsonIgnore
	@Deprecated
    private String username;

	/**
	 * Use users instead
	 */
    @JsonIgnore
	@Deprecated
    private String password;

	/**
	 * Use users instead
	 */
    @JsonIgnore
	@Deprecated
    private String usergroup;

    @JsonIgnore
	private List<User> users = new ArrayList<>();

	@SuppressWarnings("unused")
	private ServiceInstance() {
	}

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

	public ServiceInstance(String serviceInstanceId, String serviceDefintionId, String planId, String organizationGuid,
			String spaceGuid, Map<String, Object> parameters, String dashboardUrl, String internalId) {
		initialize(id, serviceDefinitionId, planId, organizationGuid, spaceGuid, parameters);
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
			String spaceGuid, Map<String, Object> parameters, Map<String, String> context) {
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

	private void setPlanId(String planId) {
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

	private void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	private void setParameters(Map<String, Object> parameters) {
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

	public Map<String, String> getContext() {
		return context;
	}

	public void setContext(Map<String, String> context) {
		this.context = new HashMap<String, String>(context);
	}

	/**
	 * Use getUsers() instead
	 */
	@Deprecated
    public String getUsername() { return username; }

	/**
	 * Use setUsers() instead
	 */
	@Deprecated
    public void setUsername(String username) { this.username = username; }

	/**
	 * Use getUsers() instead
	 */
	@Deprecated
    public String getPassword() { return password; }

	/**
	 * Use setUsers() instead
	 */
	@Deprecated
    public void setPassword(String password) { this.password = password; }

	/**
	 * Use getUsers() instead
	 */
	@Deprecated
    public String getUsergroup() { return usergroup; }

	/**
	 * Use setUsers() instead
	 */
	@Deprecated
    public void setUsergroup(String usergroup) { this.usergroup = usergroup; }

	public String getFloatingIpId() { return floatingIpId; }

	public void setFloatingIpId(String floatingIpId) { this.floatingIpId = floatingIpId; }

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}
