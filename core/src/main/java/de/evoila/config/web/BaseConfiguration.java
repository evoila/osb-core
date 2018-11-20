/**
 * 
 */
package de.evoila.config.web;

import de.evoila.cf.security.uaa.utils.HeaderCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Johannes Hiemer.
 * @author Marco Di Martino
 */
@Configuration
@EnableWebSecurity
public class BaseConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .exposedHeaders("WWW-Authenticate",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Headers"
                )
                .allowedMethods("OPTIONS", "HEAD",
                        "GET", "POST",
                        "PUT", "PATCH",
                        "DELETE", "HEAD")
                .allowCredentials(true);

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
