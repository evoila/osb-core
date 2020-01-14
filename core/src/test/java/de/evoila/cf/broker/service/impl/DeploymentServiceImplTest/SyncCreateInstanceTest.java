package de.evoila.cf.broker.service.impl.DeploymentServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SyncCreateInstanceTest extends BaseTest {

    private Map<String, Object> requestParameters;

    @BeforeEach
    void setUp() {
        super.setUp();

        requestParameters = new HashMap<>();
    }

    @Test
    void preCreateInstanceThrows() throws PlatformException {
        PlatformException platformEx = new PlatformException("Mock");
        when(platformService.preCreateInstance(serviceInstance, plan))
                .thenThrow(platformEx);
        ServiceBrokerException expectedEx = new ServiceBrokerException("Error during pre service instance creation", platformEx);
        ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                 () -> service.syncCreateInstance(serviceInstance,
                                                                                  requestParameters,
                                                                                  plan,
                                                                                  platformService));
        assertEquals(expectedEx, ex);
        assertSame(platformEx, ex.getCause());
    }

    @Nested
    class preCreateInstanceDoesNotThrow {

        @BeforeEach
        void setUp() throws PlatformException {
            when(platformService.preCreateInstance(serviceInstance, plan))
                    .thenReturn(serviceInstance);
        }

        @Test
        void createInstanceThrows() throws PlatformException, ServiceDefinitionDoesNotExistException {
            Exception[] exceptions = {
                    new PlatformException("Mock"),
                    new ServiceDefinitionDoesNotExistException("Mock")
            };
            when(platformService.createInstance(serviceInstance,
                                                plan,
                                                requestParameters))
                    .thenThrow(exceptions);
            for (Exception causeEx : exceptions) {
                ServiceBrokerException expectedEx = new ServiceBrokerException("Could not create instance due to: ", causeEx);
                ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                         () -> service.syncCreateInstance(serviceInstance,
                                                                                          requestParameters,
                                                                                          plan,
                                                                                          platformService));
                assertEquals(expectedEx, ex);
                assertSame(causeEx, ex.getCause());
            }
        }

        @Nested
        class createInstanceDoesNotThrow {

            @BeforeEach
            void setUp() throws PlatformException, ServiceDefinitionDoesNotExistException {
                when(platformService.createInstance(serviceInstance,
                                                    plan,
                                                    requestParameters))
                        .thenReturn(serviceInstance);
            }

            @Test
            void postCreateInstanceThrows() throws PlatformException {
                PlatformException platformEx = new PlatformException("Mock");
                when(platformService.postCreateInstance(serviceInstance, plan))
                        .thenThrow(platformEx);
                ServiceBrokerException expectedEx = new ServiceBrokerException("Error during post service instance creation", platformEx);
                ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                         () -> service.syncCreateInstance(serviceInstance,
                                                                                          requestParameters,
                                                                                          plan,
                                                                                          platformService));
                assertEquals(expectedEx, ex);
                assertSame(platformEx, ex.getCause());
            }

            @Test
            void postCreateInstanceDoesNotThrow() throws PlatformException, ServiceBrokerException {
                when(platformService.postCreateInstance(serviceInstance, plan))
                        .thenReturn(serviceInstance);
                ServiceInstance result = service.syncCreateInstance(serviceInstance,
                                                                    requestParameters,
                                                                    plan,
                                                                    platformService);
                verify(serviceInstanceRepository, times(1))
                        .saveServiceInstance(serviceInstance);
                assertSame(serviceInstance, result);
            }

        }

    }

}
