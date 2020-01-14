package de.evoila.cf.broker.cpi.endpoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.evoila.cf.broker.model.cpi.AvailabilityState;
import de.evoila.cf.broker.model.cpi.EndpointServiceState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EndpointAvailabilityServiceTest {

    @Mock
    private EndpointServiceState endpointServiceState;

    private EndpointAvailabilityService service;

    @BeforeEach
    void setUp() {
        service = new EndpointAvailabilityService();
    }

    @Nested
    class addMethod {

        @Nested
        class withEmptyMap {

            private void validateWith(String key, EndpointServiceState value) {
                Map<String, EndpointServiceState> expectedMap = new HashMap<>();
                expectedMap.put(key, value);
                assertEquals(expectedMap, service.getServices());
            }

            @Test
            void withNullKey() {
                service.add(null, endpointServiceState);
                validateWith(null, endpointServiceState);
            }

            @Test
            void withNullValue() {
                service.add("Mock", null);
                validateWith("Mock", null);
            }

            @Test
            void withKeyAndValue() {
                service.add("Mock", endpointServiceState);
                validateWith("Mock", endpointServiceState);
            }

        }

        @Nested
        class withNotEmptyMap {

            private final String HAPPY_KEY_1 = "KEY1";
            private final String HAPPY_KEY_2 = "KEY2";

            private Map<String, EndpointServiceState> expectedMap;

            @BeforeEach
            void setUp() {
                expectedMap = new HashMap<>();
                expectedMap.put(HAPPY_KEY_1, null);
                expectedMap.put(HAPPY_KEY_2, null);
                service.setServices(new HashMap<>(expectedMap));
            }

            @Test
            void withNewKey() {
                expectedMap.put("Mock", endpointServiceState);
                service.add("Mock", endpointServiceState);
                assertEquals(expectedMap, service.getServices());
            }

            @Test
            void withExistingKeyButSameValue() {
                service.add(HAPPY_KEY_2, null);
                assertEquals(expectedMap, service.getServices());
            }

            @Test
            void withExistingKeyButNewValue() {
                expectedMap.put(HAPPY_KEY_2, endpointServiceState);
                service.add(HAPPY_KEY_2, endpointServiceState);
                assertEquals(expectedMap, service.getServices());
            }

        }

    }

    @Nested
    class isAvailableMethod {

        private final String HAPPY_KEY_FOR_VALUE    = "KEY_VALUE";
        private final String HAPPY_KEY_FOR_NULL     = "KEY_NULL";

        @BeforeEach
        void setUp() {
            Map<String, EndpointServiceState> services = new HashMap<>();
            services.put(HAPPY_KEY_FOR_VALUE, endpointServiceState);
            services.put(HAPPY_KEY_FOR_NULL, null);
            service.setServices(services);
        }

        @Test
        void withInvalidKey() {
            boolean result = service.isAvailable("Mock");
            assertTrue(result);
        }

        @Test
        void withValidKeyButNullValue() {
            boolean result = service.isAvailable(HAPPY_KEY_FOR_NULL);
            assertTrue(result);
        }

        @Nested
        class withValidKeyAndValue {

            @Test
            void withStateAvailable() {
                when(endpointServiceState.getState())
                        .thenReturn(AvailabilityState.AVAILABLE);
                boolean result = service.isAvailable(HAPPY_KEY_FOR_VALUE);
                assertTrue(result);
            }

            @Test
            void withStateNotAvailable() {
                List<AvailabilityState> states = Arrays.stream(AvailabilityState.values())
                                                       .filter(s -> s != AvailabilityState.AVAILABLE)
                                                       .collect(Collectors.toList());
                for (AvailabilityState state : states) {
                    when(endpointServiceState.getState())
                            .thenReturn(state);
                    boolean result = service.isAvailable(HAPPY_KEY_FOR_VALUE);
                    assertFalse(result);
                }
            }

        }

    }

}
