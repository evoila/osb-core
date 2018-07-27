package de.evoila.cf.broker.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.evoila.cf.broker.model.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.List;

/** @author Johannes Hiemer. */
@Configuration
@ConfigurationProperties(prefix="endpoints")
public class EndpointConfiguration {

    @JsonProperty(value = "default")
    private String defaultEndpoint;

    @JsonProperty(value = "custom")
    private List<Server> customEndpoints;

    public String getDefault() {
        return defaultEndpoint;
    }

    public void setDefault(String defaultEndpoint) {
        this.defaultEndpoint = defaultEndpoint;
    }

    public List<Server> getCustom() {
        return customEndpoints;
    }

    public void setCustom(List<Server> customEndpoints) {
        this.customEndpoints = customEndpoints;
    }
}
