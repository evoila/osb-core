package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.evoila.cf.broker.model.catalog.MaintenanceInfo;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
public class BaseServiceInstanceRequest {

    @JsonSerialize
    @JsonProperty("context")
    protected Map<String, Object> context = new HashMap<>();

    @NotEmpty
    @JsonSerialize
    @JsonProperty("service_id")
    protected String serviceDefinitionId;

    @NotEmpty
    @JsonSerialize
    @JsonProperty("plan_id")
    protected String planId;

    @JsonSerialize
    @JsonProperty("parameters")
    protected Map<String, Object> parameters = new HashMap<>();

    @JsonSerialize
    @JsonProperty("maintenance_info")
    private MaintenanceInfo maintenanceInfo;

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public String getServiceDefinitionId() {
        return serviceDefinitionId;
    }

    public void setServiceDefinitionId(String serviceDefinitionId) {
        this.serviceDefinitionId = serviceDefinitionId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public MaintenanceInfo getMaintenanceInfo() {
        return maintenanceInfo;
    }

    public void setMaintenanceInfo(MaintenanceInfo maintenanceInfo) {
        this.maintenanceInfo = maintenanceInfo;
    }
}
