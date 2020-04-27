package de.evoila.cf.security.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "spring.ssl")
public class CustomCertificates {

    // Yaml doesn't like multiline list elements, so I decided to use map for now.
    private Map<String, String> certificates;

    public Map<String, String> getCertificates() {
        return certificates;
    }

    public Collection<String> getCertificatesAsString() {
        if (certificates != null) {
            return certificates.values();
        }
        return Collections.emptyList();
    }

    public void setCertificates(Map<String, String> certificates) {
        this.certificates = certificates;
    }
}
