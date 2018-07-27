/**
 * 
 */
package de.evoila.config.web;

import de.evoila.cf.config.security.uaa.utils.HeaderCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author Johannes Hiemer.
 * @author Marco Di Martino
 */
@Configuration
@EnableWebSecurity
public class BaseConfiguration {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");

        config.addExposedHeader("WWW-Authenticate");
        config.addExposedHeader("Access-Control-Allow-Origin");
        config.addExposedHeader("Access-Control-Allow-Headers");

        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public FilterRegistrationBean headerCheck() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HeaderCheckFilter());
        registration.addUrlPatterns(
                "/v2/catalog",
                "/v2/catalog/",
                "/v2/service_instances/*",
                "/v2/service_instances/*/last_operation",
                "/v2/service_instances/*/service_bindings/*"
        );
        return registration;
    }

}
