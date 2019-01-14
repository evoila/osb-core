package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.bean.extension.IaaSConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
@Configuration
@ConfigurationProperties(prefix = "site")
public class SiteConfiguration {

    private IaaSConfiguration iaas;

    private Map<String, Object> properties;

    public IaaSConfiguration getIaas() {
        return iaas;
    }

    public void setIaas(IaaSConfiguration iaas) {
        this.iaas = iaas;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
