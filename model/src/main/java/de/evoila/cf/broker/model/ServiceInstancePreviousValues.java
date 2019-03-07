package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.validation.constraints.NotEmpty;

/**
 * @author Johannes Hiemer.
 */
public class ServiceInstancePreviousValues {

    @JsonSerialize
    @JsonProperty("service_id")
    private String serviceId;

    @JsonSerialize
    @JsonProperty("plan_id")
    private String planId;

    @NotEmpty
    @JsonSerialize
    @JsonProperty("organization_guid")
    private String organizationGuid;

    @NotEmpty
    @JsonSerialize
    @JsonProperty("space_guid")
    private String spaceGuid;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
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
