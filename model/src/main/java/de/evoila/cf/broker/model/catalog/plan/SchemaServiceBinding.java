package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Marco Di Martino.
 */
public class SchemaServiceBinding {

	@JsonProperty(value = "create", required = false)
	private SchemaServiceCreate create;

	public SchemaServiceBinding() {
	}

	public SchemaServiceBinding(SchemaServiceCreate create) {
		this.create = create;
	}

	public SchemaServiceCreate getCreate() {
		return create;
	}

	public void setCreate(SchemaServiceCreate create) {
		this.create = create;
	}
}