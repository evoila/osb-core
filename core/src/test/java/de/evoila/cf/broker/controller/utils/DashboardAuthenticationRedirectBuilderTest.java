package de.evoila.cf.broker.controller.utils;

import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.catalog.Dashboard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardAuthenticationRedirectBuilderTest {

    private DashboardAuthenticationRedirectBuilder builder;

    @Mock
    private Dashboard dashboard;
    @Mock
    private DashboardClient dashboadClient;

    @Test
    void getRedirectUrl_Valid() throws  URISyntaxException {
        when(dashboard.getAuthEndpoint()).thenReturn("base");
        when(dashboadClient.getId()).thenReturn("123456");

        builder = new DashboardAuthenticationRedirectBuilder(dashboard, dashboadClient, "redirect", "testscope");

        String redirectUrl = builder.getRedirectUrl();
        assertEquals("base/authorize?client_id=123456&redirect_uri=redirect&response_type=code&scopes=testscope", redirectUrl);
    }

    @Test
    void getRedirectUrl_ValidEscaping() throws  URISyntaxException {
        when(dashboard.getAuthEndpoint()).thenReturn("base");
        when(dashboadClient.getId()).thenReturn("?=&123456");

        builder = new DashboardAuthenticationRedirectBuilder(dashboard, dashboadClient, "?=&redirect", "?=&testscope");

        String redirectUrl = builder.getRedirectUrl();
        assertEquals("base/authorize?client_id=%3F%3D%26123456&redirect_uri=%3F%3D%26redirect&response_type=code&scopes=%3F%3D%26testscope", redirectUrl);
    }

    @Test
    void getRedirectUrl_Invalid() {
        when(dashboard.getAuthEndpoint()).thenReturn("ba se");
        when(dashboadClient.getId()).thenReturn("123456");
        builder = new DashboardAuthenticationRedirectBuilder(dashboard, dashboadClient, "redirect", "testscope");

        assertThrows(URISyntaxException.class, () -> builder.getRedirectUrl());
    }

    @Test
    void DashboardAuthenticationRedirectBuilder_IllegalArgumentDashboard() {
        assertThrows(IllegalArgumentException.class, () -> new DashboardAuthenticationRedirectBuilder(null, dashboadClient, "redirect", "testscope"));
    }

    @Test
    void DashboardAuthenticationRedirectBuilder_IllegalArgumentClient() {
        assertThrows(IllegalArgumentException.class, () -> new DashboardAuthenticationRedirectBuilder(dashboard, null, "redirect", "testscope"));
    }
}