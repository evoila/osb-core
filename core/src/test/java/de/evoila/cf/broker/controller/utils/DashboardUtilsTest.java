package de.evoila.cf.broker.controller.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.catalog.Dashboard;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("InnerClassMayBeStatic")
@ExtendWith(MockitoExtension.class)
class DashboardUtilsTest {

    private static final String     HAPPY_DASHBOARD_URL = "https://www.test.com";
    private static final String     HAPPY_INSTANCE_ID   = "975834d6-bb9f-4825-98f2-ddaad251a6d3";
    private static final String     HAPPY_REDIRECT_URI  = "https://www.test2.com";
    private static final String[]   HAPPY_APPENDIXES    = {"first",
                                                           "second",
                                                           "third"};

    @Mock
    private ServiceDefinition serviceDefinition;
    @Mock
    private Dashboard dashboard;

    @Nested
    class hashDashboard {

        @Test
        void isTrue() {
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
        class isFalse {

            private boolean result;

            @AfterEach
            void tearDown() {
                assertFalse(result);
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            void serviceDefinitionNull() {
                result = DashboardUtils.hasDashboard(null);
            }

            @Test
            void dashboardNull() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(null);
                result = DashboardUtils.hasDashboard(serviceDefinition);
            }

            @Test
            void dashboardHasNoUrl() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn(null);
                result = DashboardUtils.hasDashboard(serviceDefinition);
            }

            @Test
            void dashboardUrlIsNoUrl() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn("noUrl");
                result = DashboardUtils.hasDashboard(serviceDefinition);
            }

            @Test
            void dashboardClientNull() {
                when(serviceDefinition.getDashboard())
                        .thenReturn(dashboard);
                when(dashboard.getUrl())
                        .thenReturn(HAPPY_DASHBOARD_URL);
                when(serviceDefinition.getDashboardClient())
                        .thenReturn(null);
                result = DashboardUtils.hasDashboard(serviceDefinition);
            }

        }

    }

    @Nested
    class dashboard {

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

    @Nested
    class redirectUri {

        @Mock
        private DashboardClient dashboardClient;

        @Nested
        class exceptionThrown {

            @Test
            void dashboardClientNull() {
                assertThrows(IllegalArgumentException.class,
                             () -> DashboardUtils.redirectUri(null, HAPPY_APPENDIXES));
            }

            @SuppressWarnings("ConfusingArgumentToVarargsMethod")
            @Test
            void appendixesNull() {
                assertThrows(IllegalArgumentException.class,
                             () -> DashboardUtils.redirectUri(dashboardClient, null));
            }

        }

        @Nested
        class success {

            private String[] getHappyAppendixesCopy() {
                String[] copy = new String[HAPPY_APPENDIXES.length];
                System.arraycopy(HAPPY_APPENDIXES, 0, copy, 0, HAPPY_APPENDIXES.length);
                return copy;
            }

            @Nested
            class redirectUriVariations {

                @Test
                void isNull() {
                    when(dashboardClient.getRedirectUri())
                            .thenReturn(null);
                    String result = DashboardUtils.redirectUri(dashboardClient, HAPPY_APPENDIXES);
                    assertEquals(HAPPY_APPENDIXES[0] + "/" +
                                 HAPPY_APPENDIXES[1] + "/" +
                                 HAPPY_APPENDIXES[2],
                                 result);
                }

                @Test
                void isEmpty() {
                    when(dashboardClient.getRedirectUri())
                            .thenReturn("");
                    String result = DashboardUtils.redirectUri(dashboardClient, HAPPY_APPENDIXES);
                    assertEquals(HAPPY_APPENDIXES[0] + "/" +
                                 HAPPY_APPENDIXES[1] + "/" +
                                 HAPPY_APPENDIXES[2],
                                 result);
                }

                @Test
                void startsWithSlash() {
                    when(dashboardClient.getRedirectUri())
                            .thenReturn("/" + HAPPY_REDIRECT_URI);
                    String result = DashboardUtils.redirectUri(dashboardClient, HAPPY_APPENDIXES);
                    assertEquals(HAPPY_REDIRECT_URI + "/" +
                                 HAPPY_APPENDIXES[0] + "/" +
                                 HAPPY_APPENDIXES[1] + "/" +
                                 HAPPY_APPENDIXES[2],
                                 result);
                }

                @Test
                void endsWithSlash() {
                    when(dashboardClient.getRedirectUri())
                            .thenReturn(HAPPY_REDIRECT_URI + "/");
                    String result = DashboardUtils.redirectUri(dashboardClient, HAPPY_APPENDIXES);
                    assertEquals(HAPPY_REDIRECT_URI + "/" +
                                 HAPPY_APPENDIXES[0] + "/" +
                                 HAPPY_APPENDIXES[1] + "/" +
                                 HAPPY_APPENDIXES[2],
                                 result);
                }

                @Test
                void startsEndsAndHasASlashWithin() {
                    when(dashboardClient.getRedirectUri())
                            .thenReturn("/" + HAPPY_REDIRECT_URI + "/extension/");
                    String result = DashboardUtils.redirectUri(dashboardClient, HAPPY_APPENDIXES);
                    assertEquals(HAPPY_REDIRECT_URI + "/extension/" +
                                 HAPPY_APPENDIXES[0] + "/" +
                                 HAPPY_APPENDIXES[1] + "/" +
                                 HAPPY_APPENDIXES[2],
                                 result);
                }

            }

            @Nested
            class appendixesVariations {

                @Nested
                class isNull {

                    @Test
                    void first() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[0] = null;
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals( HAPPY_REDIRECT_URI + "/" +
                                      copy[1] + "/" +
                                      copy[2],
                                      result);
                    }

                    @Test
                    void second() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[1] = null;
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals( HAPPY_REDIRECT_URI + "/" +
                                      copy[0] + "/" +
                                      copy[2],
                                      result);
                    }

                    @Test
                    void third() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[2] = null;
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals( HAPPY_REDIRECT_URI + "/" +
                                      copy[0] + "/" +
                                      copy[1],
                                      result);
                    }

                    @Test
                    void all() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = new String[HAPPY_APPENDIXES.length];
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals( HAPPY_REDIRECT_URI,
                                      result);
                    }

                }

                @Nested
                class isEmpty {

                    @Test
                    void first() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[0] = "";
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals( HAPPY_REDIRECT_URI + "/" +
                                      copy[1] + "/" +
                                      copy[2],
                                      result);
                    }

                    @Test
                    void second() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[1] = "";
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals( HAPPY_REDIRECT_URI + "/" +
                                      copy[0] + "/" +
                                      copy[2],
                                      result);
                    }

                    @Test
                    void third() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[2] = "";
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals( HAPPY_REDIRECT_URI + "/" +
                                      copy[0] + "/" +
                                      copy[1],
                                      result);
                    }

                    @Test
                    void all() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = new String[HAPPY_APPENDIXES.length];
                        Arrays.fill(copy, "");
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals( HAPPY_REDIRECT_URI,
                                      result);
                    }

                }

                @Nested
                class startsWithSlash {

                    @Test
                    void first() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[0] = "/" + copy[0];
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/" +
                                     HAPPY_APPENDIXES[1] + "/" +
                                     HAPPY_APPENDIXES[2],
                                     result);
                    }

                    @Test
                    void second() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[1] = "/" + copy[1];
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/" +
                                     HAPPY_APPENDIXES[1] + "/" +
                                     HAPPY_APPENDIXES[2],
                                     result);
                    }

                    @Test
                    void third() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[2] = "/" + copy[2];
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/" +
                                     HAPPY_APPENDIXES[1] + "/" +
                                     HAPPY_APPENDIXES[2],
                                     result);
                    }

                    @Test
                    void all() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        for (int i = 0; i < copy.length; ++i) {
                            copy[i] = "/" + copy[i];
                        }
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/" +
                                     HAPPY_APPENDIXES[1] + "/" +
                                     HAPPY_APPENDIXES[2],
                                     result);
                    }

                }

                @Nested
                class endsWithSlash {

                    @Test
                    void first() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] appendixes = getHappyAppendixesCopy();
                        appendixes[0] = "/" + appendixes[0];
                        String result = DashboardUtils.redirectUri(dashboardClient, appendixes);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/" +
                                     HAPPY_APPENDIXES[1] + "/" +
                                     HAPPY_APPENDIXES[2],
                                     result);
                    }

                    @Test
                    void second() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[1] = "/" + copy[1];
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/" +
                                     HAPPY_APPENDIXES[1] + "/" +
                                     HAPPY_APPENDIXES[2],
                                     result);
                    }

                    @Test
                    void third() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[2] = "/" + copy[2];
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/" +
                                     HAPPY_APPENDIXES[1] + "/" +
                                     HAPPY_APPENDIXES[2],
                                     result);
                    }

                    @Test
                    void all() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        for (int i = 0; i < copy.length; ++i) {
                            copy[i] = "/" + copy[i];
                        }
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/" +
                                     HAPPY_APPENDIXES[1] + "/" +
                                     HAPPY_APPENDIXES[2],
                                     result);
                    }

                }

                @Nested
                class startsEndsAndHasASlashWithin {

                    @Test
                    void first() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[0] = "/" + copy[0] + "/extension/";
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/extension/" +
                                     HAPPY_APPENDIXES[1] + "/" +
                                     HAPPY_APPENDIXES[2],
                                     result);
                    }

                    @Test
                    void second() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[1] = "/" + copy[1] + "/extension/";
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/" +
                                     HAPPY_APPENDIXES[1] + "/extension/" +
                                     HAPPY_APPENDIXES[2],
                                     result);
                    }

                    @Test
                    void third() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        copy[2] = "/" + copy[2] + "/extension/";
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/" +
                                     HAPPY_APPENDIXES[1] + "/" +
                                     HAPPY_APPENDIXES[2] + "/extension",
                                     result);
                    }

                    @Test
                    void all() {
                        when(dashboardClient.getRedirectUri())
                                .thenReturn(HAPPY_REDIRECT_URI);
                        String[] copy = getHappyAppendixesCopy();
                        for (int i = 0; i < copy.length; ++i) {
                            copy[i] = "/" + copy[i] + "/extension/";
                        }
                        String result = DashboardUtils.redirectUri(dashboardClient, copy);
                        assertEquals(HAPPY_REDIRECT_URI + "/" +
                                     HAPPY_APPENDIXES[0] + "/extension/" +
                                     HAPPY_APPENDIXES[1] + "/extension/" +
                                     HAPPY_APPENDIXES[2] + "/extension",
                                     result);
                    }

                }

            }

            @Test
            void bothStartEndAndHaveASlashWithin() {
                when(dashboardClient.getRedirectUri())
                        .thenReturn("/" + HAPPY_REDIRECT_URI + "/extension/");
                String[] copy = getHappyAppendixesCopy();
                for (int i = 0; i < copy.length; ++i) {
                    copy[i] = "/" + copy[i] + "/extension/";
                }
                String result = DashboardUtils.redirectUri(dashboardClient, copy);
                assertEquals(HAPPY_REDIRECT_URI + "/extension/" +
                             HAPPY_APPENDIXES[0] + "/extension/" +
                             HAPPY_APPENDIXES[1] + "/extension/" +
                             HAPPY_APPENDIXES[2] + "/extension",
                             result);
            }

        }

    }

    /**
     * A colon in the URL is treated specially by the constructor of {@link java.net.URL(String)}.
     * Therefore there are not all cases covered when using a colon in the string.
     */
    @Nested
    class isURL {

        boolean result;

        @Nested
        class valid {

            @AfterEach
            void tearDown() {
                assertTrue(result);
            }

            @Test
            void onlyOneSlashInScheme() {
                result = DashboardUtils.isURL("https:/www.test.com");
            }

            @Test
            void moreThanTwoSlashesInScheme() {
                result = DashboardUtils.isURL("https://///////////////////www.test.com");
            }

            @Test
            void randomStringWithoutColon() {
                result = DashboardUtils.isURL("https://ui.ad7.89;_P34zö5fiwej.e5lwah5gas.vffwäopppß0üä/&/()ö54öu7tOP%&T§");
            }

            @Test
            void colonWithNormalPort() {
                result = DashboardUtils.isURL("https://www.test.com:443/search");
            }

        }

        @Nested
        class invalid {

            @AfterEach
            void tearDown() {
                assertFalse(result);
            }

            @Test
            void nullString() {
                result = DashboardUtils.isURL(null);
            }

            @Test
            void emptyString() {
                result = DashboardUtils.isURL("");
            }

            @Test
            void colonFollowedByLetter() {
                result = DashboardUtils.isURL("https://www.test.com:d/search");
            }

            @Test
            void colonFollowedByNumbersAndLetter() {
                result = DashboardUtils.isURL("https://www.test.com:234d/search");
            }

            @Nested
            class scheme {

                @Test
                void missing() {
                    result = DashboardUtils.isURL("www.test.com");
                }

                @Test
                void unknownProtocol() {
                    result = DashboardUtils.isURL("xxxxxx://www.test.com");
                }

                @Test
                void noColon() {
                    result = DashboardUtils.isURL("https//www.test.com");
                }

            }

        }

    }

}
