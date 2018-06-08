package de.evoila.cf.broker.bean;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConditionOnBackupService extends AllNestedConditions {

    ConditionOnBackupService() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnBean(BackupConfiguration.class)
    static class OnBackupConfiguration {
    }

    @ConditionalOnBean(BackupTypeConfiguration.class)
    static class OnBackupTypeConfiguration {
    }
}