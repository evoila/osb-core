package de.evoila.config.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

import de.evoila.cf.security.uaa.provider.UaaRelyingPartyAuthenticationProvider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UaaSecurityConfigurationTest {

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Mock
    private UaaRelyingPartyAuthenticationProvider uaaRelyingPartyAuthenticationProvider;
    @Mock
    private WebSecurity webSecurity;
    @Mock
    private WebSecurity.IgnoredRequestConfigurer ignoredRequestConfigurer;

    private UaaSecurityConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = spy(UaaSecurityConfiguration.class);
    }

    @Nested
    class openIDRelyingPartyAuthenticationProvider {

        @Test
        void returnsValidObject() {
            assertNotNull(configuration.openIDRelyingPartyAuthenticationProvider());
        }

    }

    @Nested
    class configureAuthenticationManagerBuilder {

        @Test
        void allMethodCallsVerified() {
            doReturn(uaaRelyingPartyAuthenticationProvider)
                    .when(configuration)
                    .openIDRelyingPartyAuthenticationProvider();
            configuration.configure(authenticationManagerBuilder);
            verify(authenticationManagerBuilder, times(1))
                    .authenticationProvider(uaaRelyingPartyAuthenticationProvider);
            verifyNoMoreInteractions(authenticationManagerBuilder);
        }

    }

    @Nested
    class configureWebSecurity {

        @Test
        void allMethodCallsVerified() {
            when(webSecurity.ignoring())
                    .thenReturn(ignoredRequestConfigurer);
            when(ignoredRequestConfigurer.antMatchers(HttpMethod.GET, "/custom/v2/authentication/{serviceInstanceId}"))
                    .thenReturn(ignoredRequestConfigurer);
            when(ignoredRequestConfigurer.antMatchers(HttpMethod.GET, "/custom/v2/authentication/{serviceInstanceId}/confirm"))
                    .thenReturn(ignoredRequestConfigurer);
            configuration.configure(webSecurity);
            verifyNoMoreInteractions(webSecurity);
            verifyNoMoreInteractions(ignoredRequestConfigurer);
        }

    }

}
