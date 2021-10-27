package de.evoila.cf.broker.model.catalog;

import java.util.Objects;

/**
 * @author Johannes Hiemer
 */
public class BackupConfig {

    private String instanceGroup;

    private boolean enabled;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BackupConfig that = (BackupConfig) o;
        return enabled == that.enabled &&
                Objects.equals(instanceGroup, that.instanceGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceGroup, enabled);
    }
}
