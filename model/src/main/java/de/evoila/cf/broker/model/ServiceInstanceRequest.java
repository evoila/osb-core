package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.evoila.cf.broker.model.context.Context;

import javax.validation.constraints.NotEmpty;

/**
 * A request sent by the cloud controller to create a new instance
 * of a service.
 *
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceRequest extends BaseServiceInstanceRequest {

    @NotEmpty
    @JsonSerialize
    @JsonProperty("organization_guid")
    private String organizationGuid;

    @NotEmpty
    @JsonSerialize
    @JsonProperty("space_guid")
    private String spaceGuid;

    public ServiceInstanceRequest() {
    }

    public ServiceInstanceRequest(String serviceDefinitionId, String planId, String organizationGuid, String spaceGuid, Context context) {
        this.serviceDefinitionId = serviceDefinitionId;
        this.planId = planId;
        this.organizationGuid = organizationGuid;
        this.spaceGuid = spaceGuid;
        setContext(context);
    }

    public String getOrganizationGuid() {
        return organizationGuid;
    }

    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }

    public String getSpaceGuid() {
        return spaceGuid;
    }

    public void setSpaceGuid(String spaceGuid) {
        this.spaceGuid = spaceGuid;
    }
}
