package de.evoila.cf.broker.service.impl.DeploymentServiceImplTest;

import de.evoila.cf.broker.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.model.ServiceInstanceOperationResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
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
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_DEFINITION_ID);

        }

        @Test
        void getPlanThrows() throws ServiceDefinitionDoesNotExistException, ServiceDefinitionPlanDoesNotExistException {
            ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_PLAN_ID);
            when(serviceDefinitionRepository.getPlan(HAPPY_SERVICE_DEFINITION_ID, HAPPY_PLAN_ID))
                    .thenThrow(expectedEx);
            ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                     () -> service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
            assertSame(expectedEx, ex);
        }

        @Nested
        class getPlanDoesNotThrow {

            @BeforeEach
            void setUp() throws ServiceDefinitionDoesNotExistException, ServiceDefinitionPlanDoesNotExistException {
                when(serviceDefinitionRepository.getPlan(HAPPY_SERVICE_DEFINITION_ID, HAPPY_PLAN_ID))
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
                        service = spy(service);
                        when(platformService.isSyncPossibleOnDelete(serviceInstance))
                                .thenReturn(true);
                    }

                    @Test
                    void syncDeleteInstanceThrows() throws ServiceBrokerException {
                        PlatformException platformEx = new PlatformException("Mock");
                        ServiceBrokerException expectedEx = new ServiceBrokerException("Mock", platformEx);
                        doThrow(expectedEx)
                                .when(service)
                                .syncDeleteInstance(serviceInstance,
                                                    plan,
                                                    platformService);
                        ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                                 () -> service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
                        assertEquals(expectedEx, ex);
                        assertSame(platformEx, ex.getCause());
                    }

                    @Test
                    void syncDeleteInstanceDoesNotThrow() throws ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, ServiceDefinitionPlanDoesNotExistException {
                        doNothing()
                                .when(service)
                                .syncDeleteInstance(serviceInstance,
                                                    plan,
                                                    platformService);
                        ServiceInstanceOperationResponse response = service.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID);
                        verify(service, times(1))
                                .syncDeleteInstance(serviceInstance,
                                                    plan,
                                                    platformService);
                        assertEquals(expectedResponse, response);
                    }

                }

                @Test
                void isSyncPossibleOnDeleteReturnsFalse() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionPlanDoesNotExistException {
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
