package de.evoila.cf.broker.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author latzinger
 */
@Configuration
@ConfigurationProperties("vcap.application")
public class CloudFoundryApplicationProperties {
    private String cfApi;

    public String getCfApi() {
        return cfApi;
    }

    public void setCfApi(String cfApi) {
        this.cfApi = cfApi;
    }
}

