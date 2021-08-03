package de.evoila.cf.broker.model.catalog;

import java.util.Objects;

/**
 * @author Johannes Hiemer
 */
public class BackupConfig {

    private String instanceGroup;

    private boolean enabled;

    private String platform;

    public String getInstanceGroup() {
        return instanceGroup;
    }

    public void setInstanceGroup(String instanceGroup) {
        this.instanceGroup = instanceGroup;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BackupConfig that = (BackupConfig) o;
        return enabled == that.enabled &&
                Objects.equals(instanceGroup, that.instanceGroup) &&
                Objects.equals(platform, that.platform);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceGroup, enabled, platform);
    }
}
