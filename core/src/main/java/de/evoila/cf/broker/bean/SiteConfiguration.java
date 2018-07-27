package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.bean.utils.SiteConfigurationCondition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@ConfigurationProperties(prefix = "site")
@Conditional(SiteConfigurationCondition.class)
public class SiteConfiguration {

    Map<String, Object> properties;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
