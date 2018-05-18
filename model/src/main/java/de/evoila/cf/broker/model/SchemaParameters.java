package de.evoila.cf.broker.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class SchemaParameters {

	@JsonSerialize
	@JsonProperty("$schema")
	private String schema;
	
	@JsonSerialize
	@JsonProperty("type")
	private String type;
	
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
}
