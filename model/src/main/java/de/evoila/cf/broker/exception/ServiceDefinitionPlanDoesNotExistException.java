package de.evoila.cf.broker.exception;

import java.util.Objects;

public class ServiceDefinitionPlanDoesNotExistException extends Exception {

    private static final long serialVersionUID = -62090827040416788L;

    private String serviceDefinitionId;
    private String servicePlanId;

    public ServiceDefinitionPlanDoesNotExistException(String serviceDefinitionId, String servicePlanId) {
        this.serviceDefinitionId = serviceDefinitionId;
        this.servicePlanId = servicePlanId;
    }

    public String getMessage() {
        return "ServicePlan does not exist: ServiceDefinition = " + serviceDefinitionId +
                "ServicePlan = " + servicePlanId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ServiceDefinitionPlanDoesNotExistException that = (ServiceDefinitionPlanDoesNotExistException) o;
        return Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
               Objects.equals(servicePlanId, that.servicePlanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceDefinitionId, servicePlanId);
    }

}
