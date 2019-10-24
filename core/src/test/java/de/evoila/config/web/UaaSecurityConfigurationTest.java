package de.evoila.config.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class UaaSecurityConfigurationTest {

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

}
