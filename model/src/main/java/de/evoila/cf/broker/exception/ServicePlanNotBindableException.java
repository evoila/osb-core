package de.evoila.cf.broker.exception;

import java.util.Objects;

public class ServicePlanNotBindableException extends Exception {

    private String serviceDefinitionId;
    private String servicePlanId;

    public ServicePlanNotBindableException(String serviceDefinitionId, String servicePlanId) {
        this.serviceDefinitionId = serviceDefinitionId;
        this.servicePlanId = servicePlanId;
    }

    @Override
    public String getMessage() {
        return "Service Definition: " + serviceDefinitionId + " with Plan: " + servicePlanId + " is not bindable.";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServicePlanNotBindableException that = (ServicePlanNotBindableException) o;
        return Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
                Objects.equals(servicePlanId, that.servicePlanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceDefinitionId, servicePlanId);
    }
}
