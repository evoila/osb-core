package de.evoila.cf.broker.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/** @author Yannic Remmet */
@Configuration
@ConditionalOnProperty(prefix = "backup", name = {"enabled"})
public class BackupTypeConfiguration {

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
