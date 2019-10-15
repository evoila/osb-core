package de.evoila.cf.broker.controller.core.DeploymentServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstanceOperationResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteServiceInstanceTest extends BaseTest {

    @Test
    void getServiceInstanceThrows() throws ServiceInstanceDoesNotExistException {
        ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID);
        when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenThrow(expectedEx);
        ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                               () -> service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
        assertSame(expectedEx, ex);
    }

    @Nested
    class getServiceInstanceDoesNotThrow {

        @BeforeEach
        void setUp() throws ServiceInstanceDoesNotExistException {
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
        }

        @Test
        void getPlanThrows() throws ServiceDefinitionDoesNotExistException {
            ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_PLAN_ID);
            when(serviceDefinitionRepository.getPlan(HAPPY_PLAN_ID))
                    .thenThrow(expectedEx);
            ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                     () -> service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
            assertSame(expectedEx, ex);
        }

        @Nested
        class getPlanDoesNotThrow {

            @BeforeEach
            void setUp() throws ServiceDefinitionDoesNotExistException {
                when(serviceDefinitionRepository.getPlan(HAPPY_PLAN_ID))
                        .thenReturn(plan);
                when(plan.getPlatform())
                        .thenReturn(HAPPY_PLATFORM);
            }

            @Test
            void getPlatformServiceReturnsNull() {
                when(platformRepository.getPlatformService(HAPPY_PLATFORM))
                        .thenReturn(null);
                ServiceBrokerException expectedEx = new ServiceBrokerException("No Platform configured for " + plan.getPlatform());
                ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                         () -> service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
                assertEquals(expectedEx, ex);
            }

            @Nested
            class getPlatformServiceDoesNotReturnNull {

                private ServiceInstanceOperationResponse expectedResponse;

                @BeforeEach
                void setUp() {
                    when(platformRepository.getPlatformService(HAPPY_PLATFORM))
                            .thenReturn(platformService);
                    expectedResponse = new ServiceInstanceOperationResponse();
                }

                @Nested
                class isSyncPossibleOnDeleteReturnsTrue {

                    @BeforeEach
                    void setUp() {
                        when(platformService.isSyncPossibleOnDelete(serviceInstance))
                                .thenReturn(true);
                    }

                    @Test
                    void preDeleteInstanceThrows() throws PlatformException {
                        PlatformException platformEx = new PlatformException("Mock");
                        doThrow(platformEx)
                                .when(platformService)
                                .preDeleteInstance(serviceInstance);
                        ServiceBrokerException expectedEx = new ServiceBrokerException("Error during pre service instance deletion", platformEx);
                        ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                                 () -> service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
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
                                                                     () -> service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
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
                                                                         () -> service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
                                verifyMethodCalls();
                                assertEquals(expectedEx, ex);
                                assertSame(platformEx, ex.getCause());
                            }

                            @Test
                            void postDeleteInstanceDoesNotThrow() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException, PlatformException {
                                when(serviceInstance.getId())
                                        .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
                                ServiceInstanceOperationResponse response = service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID);
                                verifyMethodCalls();
                                verify(platformService, times(1))
                                        .postDeleteInstance(serviceInstance);
                                verify(serviceInstanceRepository, times(1))
                                        .deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID);
                                verify(jobRepository, times(1))
                                        .deleteJobProgressByReferenceId(HAPPY_SERVICE_INSTANCE_ID);
                                assertEquals(expectedResponse, response);
                            }

                        }

                    }

                }

                @Test
                void isSyncPossibleOnDeleteReturnsFalse() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
                    when(platformService.isSyncPossibleOnDelete(serviceInstance))
                            .thenReturn(false);
                    when(randomString.nextString())
                            .thenReturn(HAPPY_JOB_PROGRESS_ID);
                    expectedResponse.setOperation(HAPPY_JOB_PROGRESS_ID);
                    expectedResponse.setAsync(true);
                    ServiceInstanceOperationResponse response = service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID);
                    verify(asyncDeploymentService, times(1))
                            .asyncDeleteInstance(service,
                                                 serviceInstance,
                                                 plan,
                                                 platformService,
                                                 HAPPY_JOB_PROGRESS_ID);
                    assertEquals(expectedResponse, response);
                }

            }

        }

    }

}
