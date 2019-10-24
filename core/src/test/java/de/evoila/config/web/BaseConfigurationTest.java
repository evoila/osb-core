package de.evoila.config.web;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.List;

import de.evoila.cf.broker.interceptor.ApiVersionInterceptor;
import de.evoila.cf.broker.interceptor.OriginatingIdentityInterceptor;
import de.evoila.cf.broker.interceptor.RequestIdentityInterceptor;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseConfigurationTest {

    private BaseConfiguration configuration = new BaseConfiguration();

    @Nested
    class addCorsMappings {

        @Mock
        private CorsRegistry corsRegistry;
        @Mock
        private CorsRegistration corsRegistration;

        @Test
        void allMethodCallsVerified() {
            when(corsRegistry.addMapping("/**"))
                    .thenReturn(corsRegistration);
            when(corsRegistration.allowedOrigins("*"))
                    .thenReturn(corsRegistration);
            when(corsRegistration.allowedHeaders("*"))
                    .thenReturn(corsRegistration);
            when(corsRegistration.exposedHeaders("WWW-Authenticate",
                                                 "Access-Control-Allow-Origin",
                                                 "Access-Control-Allow-Headers"))
                    .thenReturn(corsRegistration);
            when(corsRegistration.allowedMethods("OPTIONS", "HEAD",
                                                 "GET", "POST",
                                                 "PUT", "PATCH",
                                                 "DELETE", "HEAD"))
                    .thenReturn(corsRegistration);
            when(corsRegistration.allowCredentials(true))
                    .thenReturn(corsRegistration);
            configuration.addCorsMappings(corsRegistry);
        }

    }

    @Nested
    class addInterceptors {

        @Mock
        private InterceptorRegistry interceptorRegistry;
        @Mock
        private InterceptorRegistration interceptorRegistration;

        @Test
        void allMethodCallsVerified() {
            when(interceptorRegistry.addInterceptor(any()))
                    .thenReturn(interceptorRegistration);
            when(interceptorRegistration.addPathPatterns("/**"))
                    .thenReturn(interceptorRegistration);
            when(interceptorRegistration.excludePathPatterns("/resources/**"))
                    .thenReturn(interceptorRegistration);
            configuration.addInterceptors(interceptorRegistry);
            verify(interceptorRegistration, times(3))
                    .addPathPatterns("/**");
            verify(interceptorRegistration, times(3))
                    .excludePathPatterns("/resources/**");
            ArgumentCaptor<HandlerInterceptor> interceptorCaptor = ArgumentCaptor.forClass(HandlerInterceptor.class);
            verify(interceptorRegistry, times(3))
                    .addInterceptor(interceptorCaptor.capture());
            List<HandlerInterceptor> capturedValues = interceptorCaptor.getAllValues();
            assertSame(ApiVersionInterceptor.class, capturedValues.get(0).getClass());
            assertSame(OriginatingIdentityInterceptor.class, capturedValues.get(1).getClass());
            assertSame(RequestIdentityInterceptor.class, capturedValues.get(2).getClass());
        }

    }

}
