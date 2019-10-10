package de.evoila.cf.broker.controller.utils.DashboardUtilsTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.model.DashboardClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HasDashboardTest extends BaseTest {

    @Test
    void returnsTrue() {
        DashboardClient dashboardClient = mock(DashboardClient.class);
        when(serviceDefinition.getDashboard())
                .thenReturn(dashboard);
        when(dashboard.getUrl())
                .thenReturn(HAPPY_DASHBOARD_URL);
        when(serviceDefinition.getDashboardClient())
                .thenReturn(dashboardClient);
        boolean result = DashboardUtils.hasDashboard(serviceDefinition);
        assertTrue(result);
    }

    @Nested
    class returnsFalse {

        @SuppressWarnings("ConstantConditions")
        @Test
        void serviceDefinitionNull() {
            boolean result = DashboardUtils.hasDashboard(null);
            assertFalse(result);
        }

        @Test
        void dashboardNull() {
            when(serviceDefinition.getDashboard())
                    .thenReturn(null);
            boolean result = DashboardUtils.hasDashboard(serviceDefinition);
            assertFalse(result);
        }

        @Test
        void dashboardHasNoUrl() {
            when(serviceDefinition.getDashboard())
                    .thenReturn(dashboard);
            when(dashboard.getUrl())
                    .thenReturn(null);
            boolean result = DashboardUtils.hasDashboard(serviceDefinition);
            assertFalse(result);
        }

        @Test
        void dashboardUrlIsNoUrl() {
            when(serviceDefinition.getDashboard())
                    .thenReturn(dashboard);
            when(dashboard.getUrl())
                    .thenReturn("noUrl");
            boolean result = DashboardUtils.hasDashboard(serviceDefinition);
            assertFalse(result);
        }

        @Test
        void dashboardClientNull() {
            when(serviceDefinition.getDashboard())
                    .thenReturn(dashboard);
            when(dashboard.getUrl())
                    .thenReturn(HAPPY_DASHBOARD_URL);
            when(serviceDefinition.getDashboardClient())
                    .thenReturn(null);
            boolean result = DashboardUtils.hasDashboard(serviceDefinition);
            assertFalse(result);
        }

    }

}
