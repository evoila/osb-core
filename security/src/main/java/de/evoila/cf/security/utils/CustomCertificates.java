package de.evoila.cf.security.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
@ConfigurationProperties(prefix = "spring.ssl")
public class CustomCertificates {

    private Collection<String> certificates;


    public Collection<String> getCertificates() {
        return certificates;
    }

    public void setCertificates(Collection<String> certificates) {
        this.certificates = certificates;
    }
}
