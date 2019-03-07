package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.evoila.cf.broker.model.json.schema.JsonSchema;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author Marco Di Martino, Johannes Hiemer.
 */
public class SchemaServiceUpdate {

    @NestedConfigurationProperty
	@JsonProperty(value = "parameters", required = false)
	private JsonSchema parameters;

	public SchemaServiceUpdate() {}

	public SchemaServiceUpdate(JsonSchema parameters) {
		this.parameters = parameters;
	}

	public JsonSchema getParameters() {
		return parameters;
	}

	public void setParameters(JsonSchema parameters) {
		this.parameters = parameters;
	}
}
