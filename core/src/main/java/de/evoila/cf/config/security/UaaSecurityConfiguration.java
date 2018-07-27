package de.evoila.cf.config.security;

import de.evoila.cf.config.security.uaa.UaaRelyingPartyFilter;
import de.evoila.cf.config.security.uaa.handler.CommonCorsAuthenticationEntryPoint;
import de.evoila.cf.config.security.uaa.handler.UaaRelyingPartyAuthenticationFailureHandler;
import de.evoila.cf.config.security.uaa.handler.UaaRelyingPartyAuthenticationSuccessHandler;
import de.evoila.cf.config.security.uaa.provider.UaaRelyingPartyAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@Order(2)
public class UaaSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public UaaRelyingPartyAuthenticationProvider openIDRelyingPartyAuthenticationProvider() {
        return new UaaRelyingPartyAuthenticationProvider();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder
                .authenticationProvider(openIDRelyingPartyAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        UaaRelyingPartyFilter uaaRelyingPartyFilter = new UaaRelyingPartyFilter(authenticationManager());
        uaaRelyingPartyFilter.setSuccessHandler(new UaaRelyingPartyAuthenticationSuccessHandler());
        uaaRelyingPartyFilter.setFailureHandler(new UaaRelyingPartyAuthenticationFailureHandler());

        http.addFilterBefore(uaaRelyingPartyFilter, LogoutFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .antMatcher("/custom/v2/**")
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/custom/v2/authentication/{serviceInstanceId}").permitAll()
                .antMatchers(HttpMethod.GET,"/custom/v2/authentication/{serviceInstanceId}/confirm").permitAll()
                .antMatchers(HttpMethod.GET, "/custom/v2/**").authenticated()
                .and()
                .anonymous().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .csrf().disable();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        CommonCorsAuthenticationEntryPoint entryPoint =
                new CommonCorsAuthenticationEntryPoint();
        entryPoint.setRealmName("uaaEndpointRealm");
        return entryPoint;
    }
}
