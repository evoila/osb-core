package de.evoila.config.web;

import de.evoila.cf.broker.bean.AuthenticationConfiguration;
import de.evoila.cf.broker.bean.BaseAuthenticationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Order(1)
public class BasicAuthSecurityConfiguration {

    @Autowired
    private BaseAuthenticationConfiguration authentication;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    InMemoryUserDetailsManager inMemoryAuthManager() throws Exception {
        return new InMemoryUserDetailsManager(User.builder().username(authentication.getUsername()).password(authentication.getPassword()).build());
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/v2/**")
                .authorizeHttpRequests((authz) -> authz
                .requestMatchers("/v2/**").authenticated())
                .httpBasic(withDefaults())
                .anonymous(AbstractHttpConfigurer::disable)
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authenticationEntryPoint()))
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean (name = "basicAuthenticationEntryPoint")
    public AuthenticationEntryPoint authenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint =
                new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("defaultEndpointRealm");
        return entryPoint;
    }
}
