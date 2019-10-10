package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import de.evoila.cf.broker.model.json.schema.JsonSchema;

/**
 * @author Marco Di Martino.
 */
public class SchemaServiceCreate {

	@JsonProperty(value = "parameters", required = false)
	private JsonSchema parameters;

	public SchemaServiceCreate() {}

	public SchemaServiceCreate(JsonSchema parameters) {
		this.parameters = parameters;
	}

	public JsonSchema getParameters() {
		return parameters;
	}

	public void setParameters(JsonSchema parameters) {
		this.parameters = parameters;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		SchemaServiceCreate that = (SchemaServiceCreate) o;
		return Objects.equals(parameters, that.parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parameters);
	}

}
