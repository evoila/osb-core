package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.catalog.MaintenanceInfo;

/**
 * A service plan available for a ServiceDefinition
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 * @author Johannes Strauß
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Plan {

	private String id;

	private String name;

	private String description;

	private Metadata metadata = new Metadata();

	private boolean free = true;

	private Schemas schemas;

    @JsonProperty("plan_updateable") // misspelling of attribute kept, do not change it
    private Boolean planUpdateable;

    private Boolean bindable;

	@JsonProperty("maintenance_info")
	private MaintenanceInfo maintenanceInfo;

	@JsonProperty("maximum_polling_duration")
	private Integer maximumPollingDuration;

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

	public MaintenanceInfo getMaintenanceInfo() {
		return maintenanceInfo;
	}

	public void setMaintenanceInfo(MaintenanceInfo maintenanceInfo) {
		this.maintenanceInfo = maintenanceInfo;
	}

    public Boolean isPlanUpdateable() {
        return this.planUpdateable;
    }

    public void setPlanUpdateable(Boolean planUpdateable) {
        this.planUpdateable = planUpdateable;
    }

	public Boolean isBindable() {
		return bindable;
	}

	public void setBindable(boolean bindable) {
		this.bindable = bindable;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		Plan plan = (Plan) o;
		return free == plan.free &&
			   id.equals(plan.id) &&
			   name.equals(plan.name) &&
			   description.equals(plan.description) &&
			   Objects.equals(metadata, plan.metadata) &&
			   Objects.equals(schemas, plan.schemas) &&
			   Objects.equals(planUpdateable, plan.planUpdateable) &&
			   Objects.equals(maintenanceInfo, plan.maintenanceInfo) &&
			   platform == plan.platform;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, description, metadata, free, schemas, planUpdateable, maintenanceInfo, platform);
	}

	public Integer getMaximumPollingDuration() {
		return maximumPollingDuration;
	}

	public void setMaximumPollingDuration(Integer maximumPollingDuration) {
		this.maximumPollingDuration = maximumPollingDuration;
	}
}
