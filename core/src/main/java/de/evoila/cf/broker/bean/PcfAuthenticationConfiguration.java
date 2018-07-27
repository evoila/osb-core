/**
 * 
 */
package de.evoila.cf.broker.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/** @author Johannes Hiemer */
@Configuration
@Profile("pcf")
public class PcfAuthenticationConfiguration extends BaseAuthenticationConfiguration {

    @Value("${security.user.name}")
    private String username;

    @Value("${security.user.password}")
    private String password;
}
