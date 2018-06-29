package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class SchemaServiceInstance {

	@JsonSerialize
	@JsonProperty(value="create", required=false)
	private SchemaServiceCreate create;
	
	@JsonSerialize
	@JsonProperty(value="update", required=false)
	private SchemaServiceUpdate update;
	
	public SchemaServiceInstance() {
		
	}

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
