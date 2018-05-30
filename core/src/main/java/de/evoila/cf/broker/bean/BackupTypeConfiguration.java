package de.evoila.cf.broker.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** @author Yannic Remmet */
@Configuration
@ConfigurationProperties(prefix= "backup")
@ConditionalOnProperty(prefix = "backupsssss", name = {"type"})
public class BackupTypeConfiguration {

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
