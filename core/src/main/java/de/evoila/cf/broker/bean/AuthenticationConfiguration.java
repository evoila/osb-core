package de.evoila.cf.broker.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author René Schollmeyer, Johannes Hiemer.
 **/
@Configuration
@Profile("!pcf")
@ConfigurationProperties(prefix = "login")
public class AuthenticationConfiguration extends BaseAuthenticationConfiguration {}
