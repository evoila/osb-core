package de.evoila.cf.broker.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Johannes Hiemer
 **/
@Configuration
@Profile("pcf")
public class PcfAuthenticationConfiguration extends BaseAuthenticationConfiguration {

    @Value("${security.user.name}")
    private String username;

    @Value("${security.user.password}")
    private String password;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}
