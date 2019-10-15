package de.evoila.cf.broker.controller.core.DeploymentServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SyncUpdateInstanceTest extends BaseTest {

    private Map<String, Object> requestParameters;

    @BeforeEach
    void setUp() {
        super.setUp();

        requestParameters = new HashMap<>();
    }

    @Test
    void preUpdateInstanceThrows() throws PlatformException {
        PlatformException platformEx = new PlatformException("Mock");
        when(platformService.preUpdateInstance(serviceInstance, plan))
                .thenThrow(platformEx);
        ServiceBrokerException expectedEx = new ServiceBrokerException("Error during pre service instance update", platformEx);
        ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                 () -> service.syncUpdateInstance(serviceInstance,
                                                                                  requestParameters,
                                                                                  plan,
                                                                                  platformService));
        assertEquals(expectedEx, ex);
        assertSame(platformEx, ex.getCause());
    }

    @Nested
    class preUpdateInstanceDoesNotThrow {

        @BeforeEach
        void setUp() throws PlatformException {
            when(platformService.preUpdateInstance(serviceInstance, plan))
                    .thenReturn(serviceInstance);
        }

        @Test
        void updateInstanceThrows() throws PlatformException {
            PlatformException platformEx = new PlatformException("Mock");
            when(platformService.updateInstance(serviceInstance,
                                                plan,
                                                requestParameters))
                    .thenThrow(platformEx);
            ServiceBrokerException expectedEx = new ServiceBrokerException("Could not update instance due to: ", platformEx);
            ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                     () -> service.syncUpdateInstance(serviceInstance,
                                                                                      requestParameters,
                                                                                      plan,
                                                                                      platformService));
            assertEquals(expectedEx, ex);
            assertSame(platformEx, ex.getCause());
        }

        @Nested
        class updateInstanceDoesNotThrow {

            @BeforeEach
            void setUp() throws PlatformException {
                when(platformService.updateInstance(serviceInstance,
                                                    plan,
                                                    requestParameters))
                        .thenReturn(serviceInstance);
            }

            @Test
            void postUpdateInstanceThrows() throws PlatformException {
                PlatformException platformEx = new PlatformException("Mock");
                when(platformService.postUpdateInstance(serviceInstance, plan))
                        .thenThrow(platformEx);
                ServiceBrokerException expectedEx = new ServiceBrokerException("Error during post service instance update", platformEx);
                ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                         () -> service.syncUpdateInstance(serviceInstance,
                                                                                          requestParameters,
                                                                                          plan,
                                                                                          platformService));
                assertEquals(expectedEx, ex);
                assertSame(platformEx, ex.getCause());
            }

            @Test
            void postUpdateInstanceDoesNotThrow() throws PlatformException, ServiceBrokerException {
                when(platformService.postUpdateInstance(serviceInstance, plan))
                        .thenReturn(serviceInstance);
                ServiceInstance result = service.syncUpdateInstance(serviceInstance,
                                                                      requestParameters,
                                                                      plan,
                                                                      platformService);
                verify(serviceInstanceRepository, times(1))
                        .updateServiceInstance(serviceInstance);
                assertSame(serviceInstance, result);
            }

        }

    }

}
