package de.evoila.cf.broker.controller.utils.DashboardUtilsTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.controller.utils.DashboardUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class DashboardTest extends BaseTest {

    @Nested
    class exceptionThrown {

        @Test
        void serviceDefinitionNull() {
            assertThrows(IllegalArgumentException.class,
                         () -> DashboardUtils.dashboard(null, HAPPY_INSTANCE_ID));
        }

        @Test
        void dashboardNull() {
            when(serviceDefinition.getDashboard())
                    .thenReturn(null);
            assertThrows(IllegalArgumentException.class,
                         () -> DashboardUtils.dashboard(serviceDefinition, HAPPY_INSTANCE_ID));
        }

        @Test
        void serviceInstanceIdNull() {
            when(serviceDefinition.getDashboard())
                    .thenReturn(dashboard);
            assertThrows(IllegalArgumentException.class,
                         () -> DashboardUtils.dashboard(serviceDefinition, null));
        }

        @Test
        void serviceInstanceIdEmpty() {
            when(serviceDefinition.getDashboard())
                    .thenReturn(dashboard);
            assertThrows(IllegalArgumentException.class,
                         () -> DashboardUtils.dashboard(serviceDefinition, ""));
        }

    }

    @Nested
    class success {

        @Nested
        class dashboardUrlVariations {

            @Test
            void isNull() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn(null);
                String result = DashboardUtils.dashboard(serviceDefinition, HAPPY_INSTANCE_ID);
                assertEquals("/" + HAPPY_INSTANCE_ID, result);
            }

            @Test
            void isEmpty() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn("");
                String result = DashboardUtils.dashboard(serviceDefinition, HAPPY_INSTANCE_ID);
                assertEquals("/" + HAPPY_INSTANCE_ID, result);
            }

            @Test
            void startsWithSlash() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn("/" + HAPPY_DASHBOARD_URL);
                String result = DashboardUtils.dashboard(serviceDefinition, HAPPY_INSTANCE_ID);
                assertEquals(HAPPY_DASHBOARD_URL + "/" + HAPPY_INSTANCE_ID, result);
            }

            @Test
            void endsWithSlash() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn(HAPPY_DASHBOARD_URL + "/");
                String result = DashboardUtils.dashboard(serviceDefinition, HAPPY_INSTANCE_ID);
                assertEquals(HAPPY_DASHBOARD_URL + "/" + HAPPY_INSTANCE_ID, result);
            }

            @Test
            void startsEndsAndHasASlashWithin() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn("/" + HAPPY_DASHBOARD_URL + "/extension/");
                String result = DashboardUtils.dashboard(serviceDefinition, HAPPY_INSTANCE_ID);
                assertEquals(HAPPY_DASHBOARD_URL + "/extension/" + HAPPY_INSTANCE_ID, result);
            }

        }

        @Nested
        class serviceInstanceIdVariations {

            @Test
            void startsWithSlash() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn(HAPPY_DASHBOARD_URL);
                String result = DashboardUtils.dashboard(serviceDefinition,
                                                         "/" + HAPPY_INSTANCE_ID);
                assertEquals(HAPPY_DASHBOARD_URL + "/" + HAPPY_INSTANCE_ID, result);
            }

            @Test
            void endsWithSlash() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn(HAPPY_DASHBOARD_URL);
                String result = DashboardUtils.dashboard(serviceDefinition,
                                                         HAPPY_INSTANCE_ID + "/");
                assertEquals(HAPPY_DASHBOARD_URL + "/" + HAPPY_INSTANCE_ID, result);
            }

            @Test
            void startsEndsAndHasASlashWithin() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn(HAPPY_DASHBOARD_URL);
                String result = DashboardUtils.dashboard(serviceDefinition,
                                                         "/prefix/" + HAPPY_INSTANCE_ID + "/");
                assertEquals(HAPPY_DASHBOARD_URL + "/prefix/" + HAPPY_INSTANCE_ID, result);
            }

        }

        @Test
        void bothStartEndAndHaveASlashWithin() {
            when(serviceDefinition.getDashboard())
                    .thenReturn(dashboard);
            when(dashboard.getUrl())
                    .thenReturn("/" + HAPPY_DASHBOARD_URL + "/extension/");
            String result = DashboardUtils.dashboard(serviceDefinition,
                                                     "/prefix/" + HAPPY_INSTANCE_ID + "/");
            assertEquals(HAPPY_DASHBOARD_URL + "/extension/prefix/" + HAPPY_INSTANCE_ID, result);
        }

    }

}
