package de.evoila.config.web;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import de.evoila.cf.security.uaa.UaaRelyingPartyFilter;
import de.evoila.cf.security.uaa.handler.CommonCorsAuthenticationEntryPoint;
import de.evoila.cf.security.uaa.handler.UaaRelyingPartyAuthenticationFailureHandler;
import de.evoila.cf.security.uaa.handler.UaaRelyingPartyAuthenticationSuccessHandler;
import de.evoila.cf.security.uaa.provider.UaaRelyingPartyAuthenticationProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UaaSecurityConfigurationTest {

    private static class TestUaaSecurityConfiguration extends UaaSecurityConfiguration {

        @Override
        protected AuthenticationManager authenticationManager() throws Exception {
            return super.authenticationManager();
        }

        @Override
        protected UaaRelyingPartyFilter createNewUaaRelyingPartyFilter(AuthenticationManager authenticationManager) {
            return super.createNewUaaRelyingPartyFilter(authenticationManager);
        }

    }


    private TestUaaSecurityConfiguration configuration = mock(TestUaaSecurityConfiguration.class, CALLS_REAL_METHODS);

    @Nested
    class openIDRelyingPartyAuthenticationProvider {

        @Test
        void returnsValidObject() {
            assertNotNull(configuration.openIDRelyingPartyAuthenticationProvider());
        }

    }

    @Nested
    class configureAuthenticationManagerBuilder {

        @Mock
        private AuthenticationManagerBuilder authenticationManagerBuilder;
        @Mock
        private UaaRelyingPartyAuthenticationProvider uaaRelyingPartyAuthenticationProvider;

        @Test
        void allMethodCallsVerified() {
            doReturn(uaaRelyingPartyAuthenticationProvider)
                    .when(configuration)
                    .openIDRelyingPartyAuthenticationProvider();
            configuration.configure(authenticationManagerBuilder);
            verify(authenticationManagerBuilder, times(1))
                    .authenticationProvider(uaaRelyingPartyAuthenticationProvider);
            verifyZeroInteractions(authenticationManagerBuilder);
        }

    }

    @Nested
    class configureWebSecurity {

        @Mock
        private WebSecurity webSecurity;
        @Mock
        private WebSecurity.IgnoredRequestConfigurer ignoredRequestConfigurer;

        @Test
        void allMethodCallsVerified() {
            when(webSecurity.ignoring())
                    .thenReturn(ignoredRequestConfigurer);
            when(ignoredRequestConfigurer.antMatchers(HttpMethod.GET, "/custom/v2/authentication/{serviceInstanceId}"))
                    .thenReturn(ignoredRequestConfigurer);
            when(ignoredRequestConfigurer.antMatchers(HttpMethod.GET, "/custom/v2/authentication/{serviceInstanceId}/confirm"))
                    .thenReturn(ignoredRequestConfigurer);
            configuration.configure(webSecurity);
            verifyNoMoreInteractions(webSecurity,
                    ignoredRequestConfigurer);
        }

    }

    @Nested
    class configureHttpSecurity {

        @Mock
        private HttpSecurity httpSecurity;
        @Mock
        private UaaRelyingPartyFilter uaaRelyingPartyFilter;
        @Mock
        private AuthenticationManager authenticationManager;
        @Mock
        private HttpSecurity.RequestMatcherConfigurer requestMatcherConfigurer;
        @Mock
        private CorsConfigurer<HttpSecurity> corsConfigurer;
        @Mock
        private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry;
        @Mock
        private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl;
        @Mock
        private SessionManagementConfigurer<HttpSecurity> sessionManagementConfigurer;
        @Mock
        private ExceptionHandlingConfigurer<HttpSecurity> exceptionHandlingConfigurer;
        @Mock
        private AuthenticationEntryPoint authenticationEntryPoint;
        @Mock
        private CsrfConfigurer<HttpSecurity> csrfConfigurer;

        @Nested
        class exceptionThrown {

            @Test
            void withAuthenticationManagerThrowing() throws Exception {
                Exception expectedE = new Exception();
                doThrow(expectedE)
                        .when(configuration)
                        .authenticationManager();
                Exception e = assertThrows(Exception.class,
                        () -> configuration.configure(httpSecurity));
                assertSame(expectedE, e);
            }

            @Test
            void withCorsThrowing() throws Exception {
                // Mocks
                Exception expectedE = new Exception();
                doReturn(authenticationManager)
                        .when(configuration)
                        .authenticationManager();
                doReturn(uaaRelyingPartyFilter)
                        .when(configuration)
                        .createNewUaaRelyingPartyFilter(authenticationManager);
                when(httpSecurity.requestMatchers())
                        .thenReturn(requestMatcherConfigurer);
                when(requestMatcherConfigurer.antMatchers("/custom/**"))
                        .thenReturn(requestMatcherConfigurer);
                when(requestMatcherConfigurer.and())
                        .thenReturn(httpSecurity);
                when(httpSecurity.cors())
                        .thenThrow(expectedE);
                // Method Call
                Exception e = assertThrows(Exception.class,
                        () -> configuration.configure(httpSecurity));
                // Verification
                ArgumentCaptor<UaaRelyingPartyAuthenticationSuccessHandler> successHandlerCaptor = ArgumentCaptor.forClass(UaaRelyingPartyAuthenticationSuccessHandler.class);
                verify(uaaRelyingPartyFilter, times(1))
                        .setSuccessHandler(successHandlerCaptor.capture());
                ArgumentCaptor<UaaRelyingPartyAuthenticationFailureHandler> failureHandlerCaptor = ArgumentCaptor.forClass(UaaRelyingPartyAuthenticationFailureHandler.class);
                verify(uaaRelyingPartyFilter, times(1))
                        .setFailureHandler(failureHandlerCaptor.capture());
                verifyNoMoreInteractions(authenticationManager,
                        uaaRelyingPartyFilter,
                        httpSecurity);
                assertSame(successHandlerCaptor.getValue().getClass(), UaaRelyingPartyAuthenticationSuccessHandler.class);
                assertSame(failureHandlerCaptor.getValue().getClass(), UaaRelyingPartyAuthenticationFailureHandler.class);
                assertSame(expectedE, e);
            }

        }

        @Test
        void allMethodCallsVerified() throws Exception {
            // Mocks
            doReturn(authenticationManager)
                    .when(configuration)
                    .authenticationManager();
            doReturn(uaaRelyingPartyFilter)
                    .when(configuration)
                    .createNewUaaRelyingPartyFilter(authenticationManager);
            when(httpSecurity.requestMatchers())
                    .thenReturn(requestMatcherConfigurer);
            when(requestMatcherConfigurer.antMatchers("/custom/**"))
                    .thenReturn(requestMatcherConfigurer);
            when(requestMatcherConfigurer.and())
                    .thenReturn(httpSecurity);
            when(httpSecurity.cors())
                    .thenReturn(corsConfigurer);
            when(corsConfigurer.and())
                    .thenReturn(httpSecurity);
            when(httpSecurity.addFilterBefore(uaaRelyingPartyFilter,
                    UsernamePasswordAuthenticationFilter.class))
                    .thenReturn(httpSecurity);
            when(httpSecurity.authorizeRequests())
                    .thenReturn(expressionInterceptUrlRegistry);
            when(expressionInterceptUrlRegistry.anyRequest())
                    .thenReturn(authorizedUrl);
            when(authorizedUrl.authenticated())
                    .thenReturn(expressionInterceptUrlRegistry);
            when(expressionInterceptUrlRegistry.and())
                    .thenReturn(httpSecurity);
            when(httpSecurity.sessionManagement())
                    .thenReturn(sessionManagementConfigurer);
            when(sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .thenReturn(sessionManagementConfigurer);
            when(sessionManagementConfigurer.and())
                    .thenReturn(httpSecurity);
            when(httpSecurity.exceptionHandling())
                    .thenReturn(exceptionHandlingConfigurer);
            doReturn(authenticationEntryPoint)
                    .when(configuration)
                    .authenticationEntryPoint();
            when(exceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint))
                    .thenReturn(exceptionHandlingConfigurer);
            when(exceptionHandlingConfigurer.and())
                    .thenReturn(httpSecurity);
            when(httpSecurity.csrf())
                    .thenReturn(csrfConfigurer);
            // Method call
            configuration.configure(httpSecurity);
            // Verification
            ArgumentCaptor<UaaRelyingPartyAuthenticationSuccessHandler> successHandlerCaptor = ArgumentCaptor.forClass(UaaRelyingPartyAuthenticationSuccessHandler.class);
            verify(uaaRelyingPartyFilter, times(1))
                    .setSuccessHandler(successHandlerCaptor.capture());
            ArgumentCaptor<UaaRelyingPartyAuthenticationFailureHandler> failureHandlerCaptor = ArgumentCaptor.forClass(UaaRelyingPartyAuthenticationFailureHandler.class);
            verify(uaaRelyingPartyFilter, times(1))
                    .setFailureHandler(failureHandlerCaptor.capture());
            verify(csrfConfigurer, times(1))
                    .disable();
            verifyNoMoreInteractions(authenticationManager,
                    uaaRelyingPartyFilter,
                    httpSecurity,
                    requestMatcherConfigurer,
                    corsConfigurer,
                    expressionInterceptUrlRegistry,
                    authorizedUrl,
                    sessionManagementConfigurer,
                    exceptionHandlingConfigurer,
                    authenticationEntryPoint,
                    csrfConfigurer);
            assertSame(successHandlerCaptor.getValue().getClass(), UaaRelyingPartyAuthenticationSuccessHandler.class);
            assertSame(failureHandlerCaptor.getValue().getClass(), UaaRelyingPartyAuthenticationFailureHandler.class);

        }

    }

    @Nested
    class authenticationEntryPoint {

        @Test
        void validObjectReturned() {
            CommonCorsAuthenticationEntryPoint result = (CommonCorsAuthenticationEntryPoint) configuration.authenticationEntryPoint();
            assertEquals("uaaEndpointRealm", result.getRealmName());
        }

    }

}
