package de.evoila.cf.broker.util.ServiceInstanceUtilsTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.util.ServiceInstanceUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BindingObjectTest extends BaseTest {

    private static final String HAPPY_USERNAME = "username";
    private static final String HAPPY_PASSWORD = "password";

    @Test
    void withNullList() {
        Map<String, Object> expectedResult = Collections.emptyMap();
        Map<String, Object> result = ServiceInstanceUtils.bindingObject(null,
                                                                        null,
                                                                        null,
                                                                        null);
        assertEquals(expectedResult, result);
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    @Nested
    class withNotFullySetServerAddresses {

        private List<ServerAddress> inputList = List.of(new ServerAddress("OnlyName1"),
                                                        new ServerAddress("OnlyName2"),
                                                        new ServerAddress("OnlyName3"));

        @Test
        void withOne() {
            Map<String, Object> expectedResult = new HashMap<>() {{
                put(HOSTNAME, null);
                put(PORT, 0);
            }};
            List<ServerAddress> oneItemList = inputList.subList(0, 1);
            Map<String, Object> result = ServiceInstanceUtils.bindingObject(oneItemList,
                                                                            null,
                                                                            null,
                                                                            null);
            assertEquals(expectedResult, result);
        }

        @Test
        void withThree() {
            Map<String, Object> expectedResult = new HashMap<>() {{
                put(HOSTS, new ArrayList<>() {{
                    add(new HashMap<>() {{
                        put(HOSTNAME, null);
                        put(PORT, 0);
                    }});
                    add(new HashMap<>() {{
                        put(HOSTNAME, null);
                        put(PORT, 0);
                    }});
                    add(new HashMap<>() {{
                        put(HOSTNAME, null);
                        put(PORT, 0);
                    }});
                }});
            }};
            Map<String, Object> result = ServiceInstanceUtils.bindingObject(inputList,
                                                                            null,
                                                                            null,
                                                                            null);
            assertEquals(expectedResult, result);
        }

    }

    @SuppressWarnings("InnerClassMayBeStatic")
    @Nested
    class withFullySetServerAddresses {

        private List<ServerAddress> inputList = List.of(new ServerAddress("Name1", "ip1", 1),
                                                        new ServerAddress("Name2", "ip2", 2),
                                                        new ServerAddress("Name3", "ip3", 3));

        @Nested
        class withOne {

            private List<ServerAddress> oneItemList = inputList.subList(0, 1);

            @Test
            void withoutUsernamePasswordAndConfig() {
                Map<String, Object> expectedResult = new HashMap<>() {{
                    put(HOSTNAME, inputList.get(0).getIp());
                    put(PORT, inputList.get(0).getPort());
                }};
                Map<String, Object> result = ServiceInstanceUtils.bindingObject(oneItemList,
                                                                                null,
                                                                                null,
                                                                                null);
                assertEquals(expectedResult, result);
            }

            @Nested
            class withUsernameAndPassword {

                @Test
                void withoutConfig() {
                    Map<String, Object> expectedResult = new HashMap<>() {{
                        put(HOSTNAME, inputList.get(0).getIp());
                        put(PORT, inputList.get(0).getPort());
                        put(USERNAME, HAPPY_USERNAME);
                        put(PASSWORD, HAPPY_PASSWORD);
                    }};
                    Map<String, Object> result = ServiceInstanceUtils.bindingObject(oneItemList,
                                                                                    HAPPY_USERNAME,
                                                                                    HAPPY_PASSWORD,
                                                                                    null);
                    assertEquals(expectedResult, result);
                }

                @Nested
                class withConfig {

                    @Test
                    void withNoEntries() {
                        Map<String, Object> additionalConfig = Collections.emptyMap();
                        Map<String, Object> expectedResult = new HashMap<>() {{
                            put(HOSTNAME, inputList.get(0).getIp());
                            put(PORT, inputList.get(0).getPort());
                            put(USERNAME, HAPPY_USERNAME);
                            put(PASSWORD, HAPPY_PASSWORD);
                        }};
                        Map<String, Object> result = ServiceInstanceUtils.bindingObject(oneItemList,
                                                                                        HAPPY_USERNAME,
                                                                                        HAPPY_PASSWORD,
                                                                                        additionalConfig);
                        assertEquals(expectedResult, result);
                    }

                    @Test
                    void withoutOverridingKeys() {
                        Map<String, Object> additionalConfig = new HashMap<>() {{
                            put("additional1", "value1");
                            put("additional2", "value2");
                        }};
                        Map<String, Object> expectedResult = new HashMap<>() {{
                            put(HOSTNAME, inputList.get(0).getIp());
                            put(PORT, inputList.get(0).getPort());
                            put(USERNAME, HAPPY_USERNAME);
                            put(PASSWORD, HAPPY_PASSWORD);
                            put("additional1", "value1");
                            put("additional2", "value2");
                        }};
                        Map<String, Object> result = ServiceInstanceUtils.bindingObject(oneItemList,
                                                                                        HAPPY_USERNAME,
                                                                                        HAPPY_PASSWORD,
                                                                                        additionalConfig);
                        assertEquals(expectedResult, result);
                    }

                    @Test
                    void withOverridingKeys() {
                        Map<String, Object> additionalConfig = new HashMap<>() {{
                            put(HOSTNAME, "OverriddenHostname");
                            put(PORT, "OverriddenPort");
                            put(USERNAME, "OverriddenUsername");
                            put(PASSWORD, "OverriddenPassword");
                        }};
                        Map<String, Object> result = ServiceInstanceUtils.bindingObject(oneItemList,
                                                                                        HAPPY_USERNAME,
                                                                                        HAPPY_PASSWORD,
                                                                                        additionalConfig);
                        assertEquals(additionalConfig, result);
                    }

                }

            }

        }

        @Nested
        class withThree {

            @Test
            void withoutUsernamePasswordAndConfig() {
                Map<String, Object> expectedResult = new HashMap<>() {{
                    put(HOSTS, new ArrayList<>() {{
                        for (ServerAddress serverAddress : inputList) {
                            add(new HashMap<>() {{
                                put(HOSTNAME, serverAddress.getIp());
                                put(PORT, serverAddress.getPort());
                            }});
                        }
                    }});
                }};
                Map<String, Object> result = ServiceInstanceUtils.bindingObject(inputList,
                                                                                null,
                                                                                null,
                                                                                null);
                assertEquals(expectedResult, result);
            }

            @Nested
            class withUsernameAndPassword {

                @Test
                void withoutConfig() {
                    Map<String, Object> expectedResult = new HashMap<>() {{
                        put(HOSTS, new ArrayList<>() {{
                            for (ServerAddress serverAddress : inputList) {
                                add(new HashMap<>() {{
                                    put(HOSTNAME, serverAddress.getIp());
                                    put(PORT, serverAddress.getPort());
                                }});
                            }
                        }});
                        put(USERNAME, HAPPY_USERNAME);
                        put(PASSWORD, HAPPY_PASSWORD);
                    }};
                    Map<String, Object> result = ServiceInstanceUtils.bindingObject(inputList,
                                                                                    HAPPY_USERNAME,
                                                                                    HAPPY_PASSWORD,
                                                                                    null);
                    assertEquals(expectedResult, result);
                }

                @Nested
                class withConfig {

                    @Test
                    void withNoEntries() {
                        Map<String, Object> additionalConfig = Collections.emptyMap();
                        Map<String, Object> expectedResult = new HashMap<>() {{
                            put(HOSTS, new ArrayList<>() {{
                                for (ServerAddress serverAddress : inputList) {
                                    add(new HashMap<>() {{
                                        put(HOSTNAME, serverAddress.getIp());
                                        put(PORT, serverAddress.getPort());
                                    }});
                                }
                            }});
                            put(USERNAME, HAPPY_USERNAME);
                            put(PASSWORD, HAPPY_PASSWORD);
                        }};
                        Map<String, Object> result = ServiceInstanceUtils.bindingObject(inputList,
                                                                                        HAPPY_USERNAME,
                                                                                        HAPPY_PASSWORD,
                                                                                        additionalConfig);
                        assertEquals(expectedResult, result);
                    }

                    @Test
                    void withoutOverridingKeys() {
                        Map<String, Object> additionalConfig = new HashMap<>() {{
                            put("additional1", "value1");
                            put("additional2", "value2");
                        }};
                        Map<String, Object> expectedResult = new HashMap<>() {{
                            put(HOSTS, new ArrayList<>() {{
                                for (ServerAddress serverAddress : inputList) {
                                    add(new HashMap<>() {{
                                        put(HOSTNAME, serverAddress.getIp());
                                        put(PORT, serverAddress.getPort());
                                    }});
                                }
                            }});
                            put(USERNAME, HAPPY_USERNAME);
                            put(PASSWORD, HAPPY_PASSWORD);
                            put("additional1", "value1");
                            put("additional2", "value2");
                        }};
                        Map<String, Object> result = ServiceInstanceUtils.bindingObject(inputList,
                                                                                        HAPPY_USERNAME,
                                                                                        HAPPY_PASSWORD,
                                                                                        additionalConfig);
                        assertEquals(expectedResult, result);
                    }

                    @Test
                    void withOverridingKeys() {
                        Map<String, Object> additionalConfig = new HashMap<>() {{
                            put(HOSTS, "OverriddenHosts");
                            put(PORT, "OverriddenPort");
                            put(USERNAME, "OverriddenUsername");
                            put(PASSWORD, "OverriddenPassword");
                        }};
                        Map<String, Object> result = ServiceInstanceUtils.bindingObject(inputList,
                                                                                        HAPPY_USERNAME,
                                                                                        HAPPY_PASSWORD,
                                                                                        additionalConfig);
                        assertEquals(additionalConfig, result);
                    }

                }

            }

        }

    }

}
