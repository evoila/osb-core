package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaServiceInstance {

	@JsonProperty(value="create", required=false)
	private SchemaServiceCreate create;

	@JsonProperty(value="update", required=false)
	private SchemaServiceUpdate update;

	public SchemaServiceCreate getCreate() {
		return create;
	}

	public void setCreate(SchemaServiceCreate create) {
		this.create = create;
	}

	public SchemaServiceUpdate getUpdate() {
		return update;
	}

	public void setUpdate(SchemaServiceUpdate update) {
		this.update = update;
	}
}
