/**
 * 
 */
package de.evoila.cf.broker.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** @author Rene Schollmeyer */
@Configuration
@ConfigurationProperties(prefix = "haproxy")
@ConditionalOnProperty(prefix = "haproxy", name = {"uri", "auth.token"})
public class HAProxyConfiguration {

	private String uri;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
