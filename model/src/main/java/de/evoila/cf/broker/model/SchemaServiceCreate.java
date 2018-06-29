package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class SchemaServiceCreate {

	@JsonSerialize
	@JsonProperty(value="parameters", required=false)
	private SchemaParameters parameters;
	
	public SchemaServiceCreate() {
		
	}

	public SchemaParameters getParameters() {
		return parameters;
	}

	public void setParameters(SchemaParameters parameters) {
		this.parameters = parameters;
	}
}
