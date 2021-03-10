package de.evoila.cf.broker.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 * @author Rene Schollmeyer, Johannes Hiemer.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.credhub")
@ConditionalOnProperty(prefix = "spring.credhub", name = {"url", "bosh-director"})
public class CredhubBean {

    private String url;

    private String boshDirector;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBoshDirector() {
        return boshDirector;
    }

    public void setBoshDirector(String boshDirector) {
        this.boshDirector = boshDirector;
    }

}
