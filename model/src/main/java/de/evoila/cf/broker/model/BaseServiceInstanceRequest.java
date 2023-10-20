package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.evoila.cf.broker.model.catalog.MaintenanceInfo;
import de.evoila.cf.broker.model.context.Context;

import jakarta.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Johannes Hiemer.
 */
public class BaseServiceInstanceRequest {

    @JsonSerialize
    @JsonProperty("context")
    protected Context context;

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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        if (context != null)
            context.validateContextObject();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BaseServiceInstanceRequest that = (BaseServiceInstanceRequest) o;
        return Objects.equals(context, that.context) &&
               Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
               Objects.equals(planId, that.planId) &&
               Objects.equals(parameters, that.parameters) &&
               Objects.equals(maintenanceInfo, that.maintenanceInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, serviceDefinitionId, planId, parameters, maintenanceInfo);
    }

}
