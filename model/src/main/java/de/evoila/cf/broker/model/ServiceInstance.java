package de.evoila.cf.broker.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.data.annotation.Id;
/**
 * An instance of a ServiceDefinition.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 *
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstance implements BaseEntity<String> {

	@Id
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
	private Map<String, String> parameters = new HashMap<String, String>();

	@JsonSerialize
	@JsonProperty("internal_id")
	private String internalId;

	@JsonSerialize
	@JsonProperty("hosts")
	private List<ServerAddress> hosts;
	
	@JsonSerialize
	@JsonProperty("context")
	private Map<String, String> context;

	@JsonIgnore
	private String username;

	@JsonIgnore
	private String password;

	@SuppressWarnings("unused")
	private ServiceInstance() {
	}


	public ServiceInstance(String id, String serviceDefinitionId, String planId, String organizationGuid,
			String spaceGuid, Map<String, String> parameters, String dashboardUrl) {
		initialize(id, serviceDefinitionId, planId, organizationGuid, spaceGuid, parameters, null, null);
		setDashboardUrl(dashboardUrl);
	}

	private void initialize(String id, String serviceDefinitionId, String planId, String organizationGuid,
			String spaceGuid, Map<String, String> parameters, String username, String password) {
		setId(id);
		setServiceDefinitionId(serviceDefinitionId);
		setPlanId(planId);
		setOrganizationGuid(organizationGuid);
		setSpaceGuid(spaceGuid);
		setPassword(password);
		setUsername(username);
		if (parameters != null)
			setParameters(parameters);
	}

	public ServiceInstance(String serviceInstanceId, String serviceDefintionId, String planId, String organizationGuid,
			String spaceGuid, Map<String, String> parameters, String dashboardUrl, String internalId) {

		initialize(id, serviceDefinitionId, planId, organizationGuid, spaceGuid, parameters, null, null);
		setInternalId(internalId);
		setDashboardUrl(dashboardUrl);
	}

	public ServiceInstance(ServiceInstance serviceInstance, String dashboardUrl, String internalId) {

		initialize(serviceInstance.id, serviceInstance.serviceDefinitionId, serviceInstance.planId,
				serviceInstance.organizationGuid, serviceInstance.spaceGuid, serviceInstance.parameters, serviceInstance.username, serviceInstance.password);

		this.setHosts(serviceInstance.getHosts());
		setInternalId(internalId);
		setDashboardUrl(dashboardUrl);
	}

	public ServiceInstance(ServiceInstance serviceInstance, String dashboardUrl, String internalId,
			List<ServerAddress> hosts) {
		initialize(serviceInstance.id, serviceInstance.serviceDefinitionId, serviceInstance.planId,
				serviceInstance.organizationGuid, serviceInstance.spaceGuid, serviceInstance.parameters, serviceInstance.username, serviceInstance.password);
		setInternalId(internalId);
		setDashboardUrl(dashboardUrl);
		setHosts(hosts);
	}

	//added
	public ServiceInstance(ServiceInstance serviceInstance, String internalId){
		initialize(serviceInstance.id, serviceInstance.serviceDefinitionId, serviceInstance.planId,
				serviceInstance.organizationGuid, serviceInstance.spaceGuid, serviceInstance.parameters, serviceInstance.username, serviceInstance.password);
		setInternalId(internalId);
	}

	public ServiceInstance(String serviceInstanceId, String serviceDefinitionId, String planId, String organizationGuid,
			String spaceGuid, Map<String, String> parameters, Map<String, String> context) {
		initialize(serviceInstanceId, serviceDefinitionId, planId, organizationGuid, spaceGuid, parameters, null, null);
		if(context != null)
			setContext(context);
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

	public Map<String, String> getParameters() {
		if(parameters == null)
			parameters = new HashMap<>();
		return parameters;
	}

	private void setParameters(Map<String, String> parameters) {
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
