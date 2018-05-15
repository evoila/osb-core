package de.evoila.cf.broker.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** @author Johannes Hiemer. */
@Configuration
@ConfigurationProperties(prefix="general")
public class GeneralConfiguration {

    private String endpointUrl;

    public String getEndpointUrl() {
        return this.endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }
}
