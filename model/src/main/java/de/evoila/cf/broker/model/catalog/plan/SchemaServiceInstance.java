package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		SchemaServiceInstance that = (SchemaServiceInstance) o;
		return Objects.equals(create, that.create) &&
			   Objects.equals(update, that.update);
	}

	@Override
	public int hashCode() {
		return Objects.hash(create, update);
	}

}
