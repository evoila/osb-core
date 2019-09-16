package de.evoila.cf.broker.exception;

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

}
