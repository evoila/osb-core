package de.evoila.config.web;


import de.evoila.cf.security.uaa.UaaRelyingPartyFilter;
import de.evoila.cf.security.uaa.handler.CommonCorsAuthenticationEntryPoint;
import de.evoila.cf.security.uaa.handler.UaaRelyingPartyAuthenticationFailureHandler;
import de.evoila.cf.security.uaa.handler.UaaRelyingPartyAuthenticationSuccessHandler;
import de.evoila.cf.security.uaa.provider.UaaRelyingPartyAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers(HttpMethod.GET,"/custom/v2/authentication/{serviceInstanceId}")
                .antMatchers(HttpMethod.GET,"/custom/v2/authentication/{serviceInstanceId}/confirm");
    }

    protected UaaRelyingPartyFilter createNewUaaRelyingPartyFilter(AuthenticationManager authenticationManager) {
        return new UaaRelyingPartyFilter(authenticationManager);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        UaaRelyingPartyFilter uaaRelyingPartyFilter = createNewUaaRelyingPartyFilter(authenticationManager());
        uaaRelyingPartyFilter.setSuccessHandler(new UaaRelyingPartyAuthenticationSuccessHandler());
        uaaRelyingPartyFilter.setFailureHandler(new UaaRelyingPartyAuthenticationFailureHandler());

        http.requestMatchers()
                .antMatchers("/custom/**")
                .and()
                .cors()
                .and()
                .addFilterBefore(uaaRelyingPartyFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .csrf().disable();
    }

    @Bean(name = "uaaAuthencationEntryPoint")
    public AuthenticationEntryPoint authenticationEntryPoint() {
        CommonCorsAuthenticationEntryPoint entryPoint =
                new CommonCorsAuthenticationEntryPoint();
        entryPoint.setRealmName("uaaEndpointRealm");
        return entryPoint;
    }
}
