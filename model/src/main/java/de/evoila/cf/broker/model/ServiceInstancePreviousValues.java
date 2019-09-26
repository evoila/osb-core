package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.evoila.cf.broker.model.catalog.MaintenanceInfo;

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

    @JsonSerialize
    @JsonProperty("maintenance_info")
    private MaintenanceInfo maintenanceInfo;

    @Deprecated
    public String getServiceId() {
        return serviceId;
    }

    @Deprecated
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @Deprecated
    public String getOrganizationGuid() {
        return organizationGuid;
    }

    @Deprecated
    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }

    @Deprecated
    public String getSpaceGuid() {
        return spaceGuid;
    }

    @Deprecated
    public void setSpaceGuid(String spaceGuid) {
        this.spaceGuid = spaceGuid;
    }

    public MaintenanceInfo getMaintenanceInfo() {
        return maintenanceInfo;
    }

    public void setMaintenanceInfo(MaintenanceInfo maintenanceInfo) {
        this.maintenanceInfo = maintenanceInfo;
    }
}
