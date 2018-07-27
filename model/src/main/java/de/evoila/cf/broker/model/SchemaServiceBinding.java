package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaServiceBinding {

	@JsonProperty(value="create", required=false)
	private SchemaServiceCreate create;

	public SchemaServiceCreate getCreate() {
		return create;
	}

	public void setCreate(SchemaServiceCreate create) {
		this.create = create;
	}
}