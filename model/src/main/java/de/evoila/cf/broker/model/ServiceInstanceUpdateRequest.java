package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.evoila.cf.broker.model.context.Context;

import java.util.Map;
import java.util.Objects;

/**
 * @author sgreenberg@gopivotal.com, Johannes Hiemer.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceUpdateRequest extends BaseServiceInstanceRequest {
    
	@JsonSerialize
    @JsonProperty("previous_values")
    private ServiceInstancePreviousValues previousValues;

	public ServiceInstanceUpdateRequest() {}

	public ServiceInstanceUpdateRequest(String serviceDefinitionId, String planId, Context context) {
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

    public Boolean isContextUpdate() {
	    return (parameters == null || parameters.isEmpty()) && (context != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        ServiceInstanceUpdateRequest that = (ServiceInstanceUpdateRequest) o;
        return Objects.equals(previousValues, that.previousValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), previousValues);
    }

}
