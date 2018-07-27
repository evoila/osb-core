package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A service plan available for a ServiceDefinition
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Plan {

	private String id;

	private String name;

	private String description;

	private Metadata metadata = new Metadata();

	private boolean free = true;

	private Schemas schemas;

	public Plan() {}

	/**
	 * Please use metadata section for custom mapping information
	 */
	private Platform platform;

	public Platform getPlatform() {
		return platform;
	}

	@JsonIgnore
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public void setPlatform(String platform) {
		this.platform = Platform.valueOf(platform);
	}

	public Plan(String id, String name, String description, Platform platform, boolean free) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.platform = platform;
		this.free = free;
		this.metadata = new Metadata();
	}

	public Plan(String id, String name, String description, Metadata metadata, Platform platform,
			boolean free) {
		this(id, name, description, platform, free);
		this.metadata = metadata;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public boolean isFree() {
		return free;
	}
	
	public boolean getFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public Schemas getSchemas() {
		return schemas;
	}

	public void setSchemas(Schemas schemas) {
		this.schemas = schemas;
	}
}
