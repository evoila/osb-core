package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A service plan available for a ServiceDefinition
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class Plan {

	@JsonSerialize
	@JsonProperty("id")
	private String id;

	@JsonSerialize
	@JsonProperty("name")
	private String name;

	@JsonSerialize
	@JsonProperty("description")
	private String description;

	@JsonSerialize
	@JsonProperty("metadata")
	private Metadata metadata;
	
	@JsonSerialize
	@JsonProperty(value="free", required=false)
	private boolean free = true;

	@JsonProperty(value = "plan_updateable",required = false, defaultValue = "false")
	private boolean planUpdateable;

	/**
	 * Please use metadata section for custom mapping information
	 */
	@Deprecated
	private int volumeSize;

	/**
	 * Please use metadata section for custom mapping information
	 */
	@Deprecated
	private VolumeUnit volumeUnit;

	/**
	 * Please use metadata section for custom mapping information
	 */
	private Platform platform;

	/**
	 * Please use metadata section for custom mapping information
	 */
	@Deprecated
	private String flavorId;

	/**
	 * Please use metadata section for custom mapping information
	 */
	@Deprecated
	private int connections;

	public Plan() {
		super();
	}

	public int getVolumeSize() {
		return volumeSize;
	}

	public void setVolumeSize(int volumeSize) {
		this.volumeSize = volumeSize;
	}

	public VolumeUnit getVolumeUnit() {
		return volumeUnit;
	}

	public void setVolumeUnit(VolumeUnit volumeUnit) {
		this.volumeUnit = volumeUnit;
	}

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

	public String getFlavorId() {
		return flavorId;
	}

	public void setFlavorId(String flavorId) {
		this.flavorId = flavorId;
	}

	public int getConnections() {
		return connections;
	}

	public void setConnections(int connections) {
		this.connections = connections;
	}

	public Plan(String id, String name, String description, Platform platform, int volumeSize, VolumeUnit volumeUnit,
			String flavorId, boolean free, int connections) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.platform = platform;
		this.volumeSize = volumeSize;
		this.flavorId = flavorId;
		this.connections = connections;
		this.volumeUnit = volumeUnit;
	}

	public Plan(String id, String name, String description, Metadata metadata, Platform platform,
			int volumeSize, VolumeUnit volumeUnit, String flavor, boolean free, int connections) {
		this(id, name, description, platform, volumeSize, volumeUnit, flavor, free, connections);
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

	public boolean isPlanUpdateable () {
		return planUpdateable;
	}

	public void setPlanUpdateable (boolean planUpdateable) {
		this.planUpdateable = planUpdateable;
	}
}
