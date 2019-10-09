package de.evoila.cf.broker.controller.utils.DashboardUtilsTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;

import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.model.DashboardClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RedirectUriTest extends BaseTest {

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
