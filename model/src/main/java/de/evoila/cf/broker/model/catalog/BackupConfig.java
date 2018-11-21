package de.evoila.cf.broker.model.catalog;

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
}
