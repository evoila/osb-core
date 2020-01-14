package de.evoila.cf.broker.service.availability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.ServerAddress;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Since unit tests should run fast, we set the second parameter always to false to avoid an initial
 * timeout.
 */
@ExtendWith(MockitoExtension.class)
class ServicePortAvailabilityVerifierTest {

    private static final int MAX_CONNECTION_TIMEOUTS = 10;

    @Mock
    private Logger log;
    @Mock
    private AvailabilityVerifier availabilityVerifier;

    @Mock
    private ServiceInstance serviceInstance;

    private ServicePortAvailabilityVerifier verifier;

    @BeforeEach
    void setUp() {
        verifier = new ServicePortAvailabilityVerifier();
        try {
            FieldSetter.setField(verifier,
                    verifier.getClass().getDeclaredField("log"),
                    log);
            FieldSetter.setField(verifier,
                    verifier.getClass().getDeclaredField("availabilityVerifier"),
                    availabilityVerifier);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Setting field failed", e);
        }
    }

    @Nested
    class verifyServiceAvailability {

        private List<ServerAddress> hosts = List.of(new ServerAddress("Dummy1", "ip1", 1),
                new ServerAddress("Dummy2", "ip2", 2),
                new ServerAddress("Dummy3", "ip3", 3));

        @Nested
        class returnsTrue {

            private void testForTrue() {
                boolean result = verifier.verifyServiceAvailability(serviceInstance, false);
                assertTrue(result);
            }

            @Test
            void withNoHosts() {
                when(serviceInstance.getHosts())
                        .thenReturn(Collections.emptyList());
                testForTrue();
            }

            @Nested
            class withNoConnectionTimeouts {

                @Test
                void withOneHost() {
                    when(serviceInstance.getHosts())
                            .thenReturn(hosts.subList(0, 1));
                    when(availabilityVerifier.verify(hosts.get(0).getIp(), hosts.get(0).getPort()))
                            .thenReturn(true);
                    testForTrue();
                }

                @Test
                void withAllHost() {
                    when(serviceInstance.getHosts())
                            .thenReturn(hosts);
                    when(availabilityVerifier.verify(anyString(), anyInt()))
                            .thenAnswer(new Answer<Boolean>() {

                                private int index = 0;

                                @Override
                                public Boolean answer(InvocationOnMock invocationOnMock) {
                                    ServerAddress serverAddress = hosts.get(index);
                                    ++index;
                                    if (!invocationOnMock.getArgument(0).equals(serverAddress.getIp()) ||
                                            !invocationOnMock.getArgument(1).equals(serverAddress.getPort())) {
                                        throw new InvalidUseOfMatchersException("Arguments do not match: " +
                                                Arrays.toString(invocationOnMock.getArguments()));
                                    }
                                    return true;
                                }

                            });
                    testForTrue();
                }

            }

            @Nested
            class withMaxConnectionTimeouts {

                @Test
                void withOneHost() {
                    when(serviceInstance.getHosts())
                            .thenReturn(hosts.subList(0, 1));
                    when(availabilityVerifier.verify(hosts.get(0).getIp(), hosts.get(0).getPort()))
                            .thenAnswer(new Answer<Boolean>() {

                                private int timeouts = 0;

                                @Override
                                public Boolean answer(InvocationOnMock invocationOnMock) {
                                    ServerAddress serverAddress = hosts.get(0);
                                    if (!invocationOnMock.getArgument(0).equals(serverAddress.getIp()) ||
                                            !invocationOnMock.getArgument(1).equals(serverAddress.getPort())) {
                                        throw new InvalidUseOfMatchersException("Arguments do not match: " +
                                                Arrays.toString(invocationOnMock.getArguments()));
                                    }
                                    return (timeouts++ % MAX_CONNECTION_TIMEOUTS) == 9;
                                }

                            });
                    testForTrue();
                }

                @Test
                void withAllHost() {
                    when(serviceInstance.getHosts())
                            .thenReturn(hosts);
                    when(availabilityVerifier.verify(anyString(), anyInt()))
                            .thenAnswer(new Answer<Boolean>() {

                                private int timeouts = 0;

                                @Override
                                public Boolean answer(InvocationOnMock invocationOnMock) {
                                    ServerAddress serverAddress = hosts.get(timeouts / MAX_CONNECTION_TIMEOUTS);
                                    if (!invocationOnMock.getArgument(0).equals(serverAddress.getIp()) ||
                                            !invocationOnMock.getArgument(1).equals(serverAddress.getPort())) {
                                        throw new InvalidUseOfMatchersException("Arguments do not match: " +
                                                Arrays.toString(invocationOnMock.getArguments()));
                                    }
                                    return (timeouts++ % MAX_CONNECTION_TIMEOUTS) == 9;
                                }

                            });
                    testForTrue();
                }

            }

        }

        @Nested
        class returnsFalse {

            private void testForFalse() {
                boolean result = verifier.verifyServiceAvailability(serviceInstance, false);
                assertFalse(result);
            }

            @Test
            void withOneHost() {
                when(serviceInstance.getHosts())
                        .thenReturn(hosts.subList(0, 1));
                when(availabilityVerifier.verify(hosts.get(0).getIp(), hosts.get(0).getPort()))
                        .thenAnswer(new Answer<Boolean>() {

                            private int timeouts = 0;

                            @Override
                            public Boolean answer(InvocationOnMock invocationOnMock) {
                                if (timeouts >= MAX_CONNECTION_TIMEOUTS) {
                                    throw new RuntimeException("Maximum number of test timeouts exceeded");
                                }
                                ServerAddress serverAddress = hosts.get(0);
                                if (!invocationOnMock.getArgument(0).equals(serverAddress.getIp()) ||
                                        !invocationOnMock.getArgument(1).equals(serverAddress.getPort())) {
                                    throw new InvalidUseOfMatchersException("Arguments do not match: " +
                                            Arrays.toString(invocationOnMock.getArguments()));
                                }

                                ++timeouts;
                                return false;
                            }

                        });
                testForFalse();
            }

            @Test
            void withAllHost() {
                when(serviceInstance.getHosts())
                        .thenReturn(hosts);
                when(availabilityVerifier.verify(anyString(), anyInt()))
                        .thenAnswer(new Answer<Boolean>() {

                            private int timeouts = 0;

                            @Override
                            public Boolean answer(InvocationOnMock invocationOnMock) {
                                ServerAddress serverAddress = hosts.get(timeouts / MAX_CONNECTION_TIMEOUTS);
                                if (!invocationOnMock.getArgument(0).equals(serverAddress.getIp()) ||
                                        !invocationOnMock.getArgument(1).equals(serverAddress.getPort())) {
                                    throw new InvalidUseOfMatchersException("Arguments do not match: " +
                                            Arrays.toString(invocationOnMock.getArguments()));
                                }
                                ++timeouts;
                                return false;
                            }

                        });
                testForFalse();
            }

        }

    }

}
