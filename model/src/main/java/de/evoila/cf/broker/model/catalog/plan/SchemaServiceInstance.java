package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Marco Di Martino.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchemaServiceInstance {

	@JsonProperty(value = "create", required = false)
	private SchemaServiceCreate create;

	@JsonProperty(value = "update", required = false)
	private SchemaServiceUpdate update;

	public SchemaServiceInstance() {}

	public SchemaServiceInstance(SchemaServiceCreate create, SchemaServiceUpdate update) {
		this.create = create;
		this.update = update;
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
