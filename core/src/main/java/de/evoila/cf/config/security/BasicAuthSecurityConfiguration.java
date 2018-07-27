package de.evoila.cf.config.security;

import de.evoila.cf.broker.bean.BaseAuthenticationConfiguration;
import de.evoila.cf.config.security.uaa.UaaRelyingPartyFilter;
import de.evoila.cf.config.security.uaa.handler.UaaRelyingPartyAuthenticationFailureHandler;
import de.evoila.cf.config.security.uaa.handler.UaaRelyingPartyAuthenticationSuccessHandler;
import de.evoila.cf.config.security.uaa.utils.Endpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
@Order(2)
public class BasicAuthSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private BaseAuthenticationConfiguration authentication;

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User
                .withUsername(authentication.getUsername())
                .password(encoder().encode(authentication.getPassword()))
                .roles("USER").build());
        return manager;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint =
                new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("defaultEndpointRealm");
        return entryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        UaaRelyingPartyFilter uaaRelyingPartyFilter = new UaaRelyingPartyFilter(authenticationManager());
        uaaRelyingPartyFilter.setSuccessHandler(new UaaRelyingPartyAuthenticationSuccessHandler());
        uaaRelyingPartyFilter.setFailureHandler(new UaaRelyingPartyAuthenticationFailureHandler());

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, Endpoints.V2_ENDPOINT).authenticated()
                .antMatchers(HttpMethod.GET, Endpoints.V2_CATALOG).authenticated()
                .antMatchers(HttpMethod.GET, Endpoints.V2_CATALOG_EXT).authenticated()
                .antMatchers(Endpoints.V2_SERVICE_INSTANCES).authenticated()
                .antMatchers(HttpMethod.GET, Endpoints.INFO).authenticated()
                .antMatchers(HttpMethod.GET, Endpoints.HEALTH).authenticated()
                .antMatchers(HttpMethod.GET, Endpoints._ERROR).authenticated()

                .antMatchers(HttpMethod.GET, Endpoints.ENV).authenticated()
                .antMatchers(HttpMethod.GET, Endpoints.V2_DASHBOARD_SID).permitAll()
                .antMatchers(HttpMethod.GET, Endpoints.V2_DASHBOARD_SID_CONFIRM).permitAll()
                .antMatchers(Endpoints.V2_BACKUP+"/**").permitAll()
                .antMatchers(Endpoints.V2_DASHBOARD_MANAGE+"/**").authenticated()
                .and()
                .httpBasic()
                .and()
                .anonymous().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .csrf().disable();
    }

}
