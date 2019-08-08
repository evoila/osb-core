package de.evoila.cf.broker.model.catalog;

public class MaintenanceInfo {

    private String version;

    private String description;

    public MaintenanceInfo() {
    }

    public MaintenanceInfo(String version, String description) {
        this.version = version;
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
