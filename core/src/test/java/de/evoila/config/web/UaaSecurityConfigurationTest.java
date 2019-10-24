package de.evoila.config.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import de.evoila.cf.security.uaa.provider.UaaRelyingPartyAuthenticationProvider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UaaSecurityConfigurationTest {

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Mock
    private UaaRelyingPartyAuthenticationProvider uaaRelyingPartyAuthenticationProvider;

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

}
