/**
 *
 */
package de.evoila.config.web;

import de.evoila.cf.broker.interceptor.ApiVersionInterceptor;
import de.evoila.cf.broker.interceptor.RequestIdentityInterceptor;
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

    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(new ApiVersionInterceptor()).addPathPatterns("/**").excludePathPatterns("/resources/**");
        registry.addInterceptor(new RequestIdentityInterceptor()).addPathPatterns("/**").excludePathPatterns("/resources/**");
    }
}