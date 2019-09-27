package de.evoila.cf.broker.model.context;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cloudfoundry extends Context {

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

    public Cloudfoundry() {
        // Jackson does not set property platform as it is for it's inheritance model. Therefor setting it manually here.
        super("cloudfoundry");
    }

    public Cloudfoundry(String organizationGuid,
                        String organizationName,
                        String spaceGuid,
                        String spaceName, String instance_name) {
        super("cloudfoundry");
        this.organizationGuid = organizationGuid;
        this.organizationName = organizationName;
        this.spaceGuid = spaceGuid;
        this.spaceName = spaceName;
        this.instanceName = instance_name;
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
}
