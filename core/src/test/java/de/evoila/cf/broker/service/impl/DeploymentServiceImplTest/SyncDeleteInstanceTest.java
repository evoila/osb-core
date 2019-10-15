package de.evoila.cf.broker.service.impl.DeploymentServiceImplTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SyncDeleteInstanceTest extends BaseTest {

    @Test
    void preDeleteInstanceThrows() throws PlatformException {
        PlatformException platformEx = new PlatformException("Mock");
        doThrow(platformEx)
                .when(platformService)
                .preDeleteInstance(serviceInstance);
        ServiceBrokerException expectedEx = new ServiceBrokerException("Error during pre service instance deletion", platformEx);
        ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                 () -> service.syncDeleteInstance(serviceInstance,
                                                                                  plan,
                                                                                  platformService));
        assertEquals(expectedEx, ex);
        assertSame(platformEx, ex.getCause());
    }

    @Nested
    class preDeleteInstanceDoesNotThrow {

        private void verifyMethodCalls() throws PlatformException {
            verify(platformService, times(1))
                    .preDeleteInstance(serviceInstance);
        }

        @Test
        void deleteInstanceThrows() throws PlatformException {
            PlatformException platformEx = new PlatformException("Mock");
            doThrow(platformEx)
                    .when(platformService)
                    .deleteInstance(serviceInstance, plan);
            ServiceBrokerException expectedEx = new ServiceBrokerException("Error during deletion of service", platformEx);
            ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                     () -> service.syncDeleteInstance(serviceInstance,
                                                                                      plan,
                                                                                      platformService));
            verifyMethodCalls();
            assertEquals(expectedEx, ex);
            assertSame(platformEx, ex.getCause());
        }

        @Nested
        class deleteInstanceDoesNotThrow {

            private void verifyMethodCalls() throws PlatformException {
                preDeleteInstanceDoesNotThrow.this.verifyMethodCalls();
                verify(platformService, times(1))
                        .deleteInstance(serviceInstance, plan);
            }

            @Test
            void postDeleteInstanceThrows() throws PlatformException {
                PlatformException platformEx = new PlatformException("Mock");
                doThrow(platformEx)
                        .when(platformService)
                        .postDeleteInstance(serviceInstance);
                ServiceBrokerException expectedEx = new ServiceBrokerException("Error during pre service instance deletion", platformEx);
                ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                         () -> service.syncDeleteInstance(serviceInstance,
                                                                                          plan,
                                                                                          platformService));
                verifyMethodCalls();
                assertEquals(expectedEx, ex);
                assertSame(platformEx, ex.getCause());
            }

            @Test
            void postDeleteInstanceDoesNotThrow() throws ServiceBrokerException, PlatformException {
                when(serviceInstance.getId())
                        .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
                service.syncDeleteInstance(serviceInstance,
                                           plan,
                                           platformService);
                verifyMethodCalls();
                verify(platformService, times(1))
                        .postDeleteInstance(serviceInstance);
                verify(serviceInstanceRepository, times(1))
                        .deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID);
                verify(jobRepository, times(1))
                        .deleteJobProgressByReferenceId(HAPPY_SERVICE_INSTANCE_ID);
            }

        }

    }

}
