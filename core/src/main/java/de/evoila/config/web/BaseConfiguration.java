/**
 *
 */
package de.evoila.config.web;

import de.evoila.cf.broker.bean.CloudFoundryApplicationProperties;
import de.evoila.cf.broker.interceptor.ApiVersionInterceptor;
import de.evoila.cf.broker.interceptor.OriginatingIdentityInterceptor;
import de.evoila.cf.broker.interceptor.RequestIdentityInterceptor;
import de.evoila.cf.broker.interceptor.ServiceInstancePermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    CloudFoundryApplicationProperties cloudFoundryApplicationProperties;

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
        registry.addInterceptor(new OriginatingIdentityInterceptor()).addPathPatterns("/**").excludePathPatterns("/resources/**");
        registry.addInterceptor(new RequestIdentityInterceptor()).addPathPatterns("/**").excludePathPatterns("/resources/**");
        registry.addInterceptor(new ServiceInstancePermissionInterceptor(cloudFoundryApplicationProperties)).addPathPatterns("/custom/**").excludePathPatterns("/custom/v2/authentication/{serviceInstanceId}/confirm", "/custom/v2/authentication/{serviceInstanceId}", "/resources/**");
    }
}