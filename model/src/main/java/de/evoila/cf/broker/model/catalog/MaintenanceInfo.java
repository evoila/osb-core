package de.evoila.cf.broker.model.catalog;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        MaintenanceInfo that = (MaintenanceInfo) o;
        return version.equals(that.version) &&
               Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, description);
    }

}
