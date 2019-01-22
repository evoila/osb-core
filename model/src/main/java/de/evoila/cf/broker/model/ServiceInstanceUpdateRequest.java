package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;

/**
 * @author sgreenberg@gopivotal.com, Johannes Hiemer.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceUpdateRequest extends BaseServiceInstanceRequest {
    
	@JsonSerialize
    @JsonProperty("previous_values")
    private ServiceInstancePreviousValues previousValues;

	public ServiceInstanceUpdateRequest() {}

	public ServiceInstanceUpdateRequest(String serviceDefinitionId, String planId, Map<String, String> context) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		setContext(context);
	}

    public ServiceInstancePreviousValues getPreviousValues() {
        return previousValues;
    }

    public void setPreviousValues(ServiceInstancePreviousValues previousValues) {
        this.previousValues = previousValues;
    }
}
