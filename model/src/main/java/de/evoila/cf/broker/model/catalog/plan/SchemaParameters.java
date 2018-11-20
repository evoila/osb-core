package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Map;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class SchemaParameters {

	@JsonSerialize
	@JsonProperty("$schema")
	private String schema;

	@JsonSerialize
	@JsonProperty("type")
	private String type;

	@JsonSerialize
	@JsonInclude(Include.NON_NULL)
	@JsonProperty(value = "required", required = false)
	private List<String> required;

	@JsonSerialize
	@JsonProperty("properties")
	private Map<String, SchemaProperty> properties;


	public SchemaParameters() {
	}

	public SchemaParameters(String schema, String type) {
		this.schema = schema;
		this.type = type;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, SchemaProperty> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, SchemaProperty> properties) {
		this.properties = properties;
	}

	public List<String> getRequired() {
		return required;
	}

	public void setRequired(List<String> required) {
		this.required = required;
	}
}
