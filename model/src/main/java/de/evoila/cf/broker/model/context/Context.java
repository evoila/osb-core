package de.evoila.cf.broker.model.context;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Context {

    @JsonProperty("platform")
    private String platform;

    @JsonProperty("organization_guid")
    private String organizationGuid;

    @JsonProperty("organization_name")
    private String organizationName;

    @JsonProperty("space_guid")
    private String spaceGuid;

    @JsonProperty("space_name")
    private String spaceName;

    @JsonProperty("instance_name")
    private String instanceName;

    @JsonProperty("clusterid")
    private String clusterId;

    @JsonProperty("namespace")
    private String namespace;

    private Map<String, Object> additionalFields;

    public Context() {
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOrganizationGuid() {
        return organizationGuid;
    }

    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getSpaceGuid() {
        return spaceGuid;
    }

    public void setSpaceGuid(String spaceGuid) {
        this.spaceGuid = spaceGuid;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    @JsonAnySetter
    public void setAdditionalFields(String key, Object value) {
        if (additionalFields == null) {
            additionalFields = new HashMap<>();
        }

        this.additionalFields.put(key, value);
    }
}
