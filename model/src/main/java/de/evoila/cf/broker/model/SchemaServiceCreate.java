package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaServiceCreate {

	@JsonProperty(value="parameters", required=false)
	private SchemaParameters parameters;

	public SchemaParameters getParameters() {
		return parameters;
	}

	public void setParameters(SchemaParameters parameters) {
		this.parameters = parameters;
	}
}
