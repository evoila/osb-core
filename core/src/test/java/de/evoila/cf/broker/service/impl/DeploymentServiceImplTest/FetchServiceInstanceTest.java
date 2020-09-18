package de.evoila.cf.broker.service.impl.DeploymentServiceImplTest;

import de.evoila.cf.broker.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class FetchServiceInstanceTest extends BaseTest {

    private void validateServiceInstanceNotFoundException(Exception ex) {
        ServiceInstanceNotFoundException expectedEx = new ServiceInstanceNotFoundException();
        assertEquals(expectedEx.getClass(), ex.getClass());
        assertEquals(expectedEx.getMessage(), ex.getMessage());
    }

    @Test
    void getServiceInstanceThrows() throws ServiceInstanceDoesNotExistException {
        when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenThrow(new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID));
        ServiceInstanceNotFoundException ex = assertThrows(ServiceInstanceNotFoundException.class,
                                                           () -> service.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
        validateServiceInstanceNotFoundException(ex);
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
            when(serviceDefinitionRepository.getPlan(HAPPY_SERVICE_DEFINITION_ID, HAPPY_PLAN_ID))
                    .thenThrow(new ServiceDefinitionDoesNotExistException(HAPPY_PLAN_ID));
            ServiceInstanceNotFoundException ex = assertThrows(ServiceInstanceNotFoundException.class,
                                                               () -> service.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
            validateServiceInstanceNotFoundException(ex);
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
                ServiceInstanceNotFoundException ex = assertThrows(ServiceInstanceNotFoundException.class,
                                                                   () -> service.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
                validateServiceInstanceNotFoundException(ex);
            }

            @Nested
            class getPlatformServiceDoesNotReturnNull {

                @BeforeEach
                void setUp() {
                    when(platformRepository.getPlatformService(HAPPY_PLATFORM))
                            .thenReturn(platformService);
                }

                @Test
                void getInstanceThrows() throws PlatformException {
                    when(platformService.getInstance(serviceInstance, plan))
                            .thenThrow(new PlatformException("Mock"));
                    ServiceInstanceNotFoundException ex = assertThrows(ServiceInstanceNotFoundException.class,
                                                                       () -> service.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
                    validateServiceInstanceNotFoundException(ex);
                }

                @Nested
                class getInstanceDoesNotThrow {

                    @BeforeEach
                    void setUp() throws PlatformException {
                        // This mock makes sure that PlatformService#getInstance gets called
                        when(platformService.getInstance(serviceInstance, plan))
                                .thenReturn(serviceInstance);
                    }

                    @Nested
                    class containsJobProgressReturnsTrue {

                        @BeforeEach
                        void setUp() {
                            when(jobRepository.containsJobProgress(HAPPY_SERVICE_INSTANCE_ID))
                                    .thenReturn(true);
                            when(jobRepository.getJobProgressByReferenceId(HAPPY_SERVICE_INSTANCE_ID))
                                    .thenReturn(jobProgress);
                        }

                        @Nested
                        class operationStateIsInProgress {

                            @BeforeEach
                            void setUp() {
                                when(jobProgress.getState())
                                        .thenReturn(JobProgress.IN_PROGRESS);
                            }

                            @Test
                            void operationIsProvision() {
                                when(jobProgress.getOperation())
                                        .thenReturn(JobProgress.PROVISION);
                                ServiceInstanceNotFoundException ex = assertThrows(ServiceInstanceNotFoundException.class,
                                                                                   () -> service.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
                                validateServiceInstanceNotFoundException(ex);
                            }

                            @Test
                            void operationIsUpdate() {
                                when(jobProgress.getOperation())
                                        .thenReturn(JobProgress.UPDATE);
                                assertThrows(ConcurrencyErrorException.class,
                                             () -> service.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
                            }

                        }

                        @Test
                        void operationStateIsNotInProgress() throws ConcurrencyErrorException, ServiceInstanceNotFoundException {
                            String[] states = {JobProgress.SUCCESS, JobProgress.FAILED, JobProgress.UNKNOWN};
                            String[] operations = { JobProgress.PROVISION, JobProgress.UPDATE};

                            for (String state : states) {
                                for (String operation : operations) {
                                    when(jobProgress.getOperation())
                                            .thenReturn(operation);
                                    when(jobProgress.getState())
                                            .thenReturn(state);
                                    ServiceInstance result = service.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID);
                                    assertSame(serviceInstance, result);
                                }
                            }
                        }
                    }

                    @Test
                    void containsJobProgressReturnsFalse() throws ConcurrencyErrorException, ServiceInstanceNotFoundException {
                        when(jobRepository.containsJobProgress(HAPPY_SERVICE_INSTANCE_ID))
                                .thenReturn(false);
                        ServiceInstance result = service.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID);
                        assertSame(serviceInstance, result);
                    }

                }

            }

        }

    }

}
