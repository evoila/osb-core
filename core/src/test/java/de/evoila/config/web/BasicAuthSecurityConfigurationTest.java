package de.evoila.config.web;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import de.evoila.cf.broker.bean.BaseAuthenticationConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicAuthSecurityConfigurationTest {

    @Mock
    private BaseAuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private BasicAuthSecurityConfiguration configuration = mock(BasicAuthSecurityConfiguration.class, CALLS_REAL_METHODS);

    @Nested
    class passwordEncoder {

        @Test
        void returnsValidObject() {
            BCryptPasswordEncoder result = (BCryptPasswordEncoder) configuration.passwordEncoder();
            assertNotNull(result);
        }

    }

    @Nested
    class configureAuthenticationManagerBuilder {

        private final String HAPPY_USERNAME = "username";
        private final String HAPPY_PASSWORD = "password";

        @Mock
        private AuthenticationManagerBuilder authenticationManagerBuilder;
        @Mock
        private InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> managerConfigurer;
        @Mock
        private UserDetailsManagerConfigurer<AuthenticationManagerBuilder, InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder>>.UserDetailsBuilder userDetailsBuilder;
        @Mock
        private PasswordEncoder passwordEncoder;

        @Nested
        class exceptionThrown {

            @Test
            void withInMemoryAuthenticationThrowing() throws Exception {
                Exception expectedE = new Exception();
                when(authenticationManagerBuilder.inMemoryAuthentication())
                        .thenThrow(expectedE);
                Exception e = assertThrows(Exception.class,
                                           () -> configuration.configure(authenticationManagerBuilder));
                assertSame(expectedE, e);
            }

        }

        @Test
        void allMethodCallsVerified() throws Exception {
            // Mocks
            when(authenticationManagerBuilder.inMemoryAuthentication())
                    .thenReturn(managerConfigurer);
            when(authenticationConfiguration.getUsername())
                    .thenReturn(HAPPY_USERNAME);
            doReturn(userDetailsBuilder)
                    .when(managerConfigurer)
                    .withUser(HAPPY_USERNAME);
            doReturn(passwordEncoder)
                    .when(configuration)
                    .passwordEncoder();
            when(authenticationConfiguration.getPassword())
                    .thenReturn(HAPPY_PASSWORD);
            when(passwordEncoder.encode(HAPPY_PASSWORD))
                    .thenReturn(HAPPY_PASSWORD);
            when(userDetailsBuilder.password(HAPPY_PASSWORD))
                    .thenReturn(userDetailsBuilder);
            when(userDetailsBuilder.authorities("USER"))
                    .thenReturn(userDetailsBuilder);
            // Method call
            configuration.configure(authenticationManagerBuilder);
            // Verification
            verifyNoMoreInteractions(authenticationManagerBuilder,
                                     managerConfigurer,
                                     userDetailsBuilder,
                                     passwordEncoder,
                                     authenticationConfiguration);
        }

    }

    @Nested
    class configureHttpSecurity {

        @Mock
        private HttpSecurity httpSecurity;
        @Mock
        private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry;
        @Mock
        private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl;
        @Mock
        private HttpBasicConfigurer<HttpSecurity> httpBasicConfigurer;
        @Mock
        private AnonymousConfigurer<HttpSecurity> anonymousConfigurer;
        @Mock
        private ExceptionHandlingConfigurer<HttpSecurity> exceptionHandlingConfigurer;
        @Mock
        private AuthenticationEntryPoint authenticationEntryPoint;
        @Mock
        private CsrfConfigurer<HttpSecurity> csrfConfigurer;

        @Nested
        class exceptionThrown {

            @Test
            void withAuthorizeRequestsThrowing() throws Exception {
                Exception expectedE = new Exception();
                when(httpSecurity.antMatcher("/v2/**"))
                        .thenReturn(httpSecurity);
                when(httpSecurity.authorizeRequests())
                        .thenThrow(expectedE);
                Exception e = assertThrows(Exception.class,
                                           () -> configuration.configure(httpSecurity));
                assertSame(expectedE, e);
            }

        }

        @Test
        void allMethodCallsVerified() throws Exception {
            // Mocks
            when(httpSecurity.antMatcher("/v2/**"))
                    .thenReturn(httpSecurity);
            when(httpSecurity.authorizeRequests())
                    .thenReturn(expressionInterceptUrlRegistry);
            when(expressionInterceptUrlRegistry.antMatchers("/v2/**"))
                    .thenReturn(authorizedUrl);
            when(authorizedUrl.authenticated())
                    .thenReturn(expressionInterceptUrlRegistry);
            when(expressionInterceptUrlRegistry.and())
                    .thenReturn(httpSecurity);
            when(httpSecurity.httpBasic())
                    .thenReturn(httpBasicConfigurer);
            when(httpBasicConfigurer.and())
                    .thenReturn(httpSecurity);
            when(httpSecurity.anonymous())
                    .thenReturn(anonymousConfigurer);
            when(anonymousConfigurer.disable())
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
            when(csrfConfigurer.disable())
                    .thenReturn(httpSecurity);
            // Method call
            configuration.configure(httpSecurity);
            // Verification
            verifyNoMoreInteractions(httpSecurity,
                                     expressionInterceptUrlRegistry,
                                     authorizedUrl,
                                     httpBasicConfigurer,
                                     anonymousConfigurer,
                                     exceptionHandlingConfigurer,
                                     authenticationEntryPoint,
                                     csrfConfigurer);
        }

    }

    @Nested
    class authenticationEntryPoint {

        @Test
        void validObjectReturned() {
            BasicAuthenticationEntryPoint result = (BasicAuthenticationEntryPoint) configuration.authenticationEntryPoint();
            assertEquals("defaultEndpointRealm", result.getRealmName());
        }

    }

}
