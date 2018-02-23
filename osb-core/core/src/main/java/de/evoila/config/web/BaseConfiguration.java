/**
 * 
 */
package de.evoila.config.web;

import de.evoila.cf.config.security.uaa.utils.HeaderCheckFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.config.web.cors.CORSFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Johannes Hiemer.
 * @author Marco Di Martino
 *
 */
@Configuration
@EnableConfigurationProperties(Catalog.class)
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

        final CorsFilter bean = new CorsFilter((source));
        return bean;
    }

    @Bean
    public FilterRegistrationBean headerCheck() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HeaderCheckFilter());
        registration.addUrlPatterns("/", "/*", "/v2/*");
        // In case you want the filter to apply to specific URL patterns only
        //registration.addUrlPatterns("/v2/*");
        return registration;
    }

}
