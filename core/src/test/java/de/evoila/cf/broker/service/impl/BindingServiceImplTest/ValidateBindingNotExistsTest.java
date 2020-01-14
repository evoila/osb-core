package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ValidateBindingNotExistsTest extends BaseTest {

    @Nested
    class validationSuccessful {

        private void validate() throws ServiceInstanceBindingExistsException, ServiceInstanceDoesNotExistException {
            service.validateBindingNotExists(request,
                                             HAPPY_BINDING_ID,
                                             HAPPY_SERVICE_INSTANCE_ID);
        }

        @Test
        void withIdNotInBindingRepo() throws ServiceInstanceBindingExistsException, ServiceInstanceDoesNotExistException {
            when(bindingRepository.containsInternalBindingId(HAPPY_BINDING_ID))
                    .thenReturn(false);
            validate();
        }

        @Test
        void withExceptionWhileGettingJobProgress() throws ServiceInstanceBindingExistsException, ServiceInstanceDoesNotExistException {
            when(bindingRepository.containsInternalBindingId(HAPPY_BINDING_ID))
                    .thenReturn(true);
            when(jobRepository.getJobProgressByReferenceId(HAPPY_BINDING_ID))
                    .thenThrow(new NoSuchElementException());
            validate();
        }

        @Nested
        class withJob {

            @BeforeEach
            void setUp() {
                when(bindingRepository.containsInternalBindingId(HAPPY_BINDING_ID))
                        .thenReturn(true);
                when(jobRepository.getJobProgressByReferenceId(HAPPY_BINDING_ID))
                        .thenReturn(jobProgress);
            }

            @Nested
            class whichIsNoBindingJob {

                @BeforeEach
                void setUp() {
                    when(jobProgress.isBinding())
                            .thenReturn(false);
                }

                @Test
                void whichIsInProgress() throws ServiceInstanceBindingExistsException, ServiceInstanceDoesNotExistException {
                    when(jobProgress.isInProgress())
                            .thenReturn(true);
                    validate();
                }

                @Test
                void whichIsNotInProgress() throws ServiceInstanceBindingExistsException, ServiceInstanceDoesNotExistException {
                    when(jobProgress.isInProgress())
                            .thenReturn(false);
                    validate();
                }

            }

            @Test
            void whichIsABindingJobButInProgress() throws ServiceInstanceBindingExistsException, ServiceInstanceDoesNotExistException {
                when(jobProgress.isBinding())
                        .thenReturn(true);
                when(jobProgress.isInProgress())
                        .thenReturn(true);
                validate();
            }

        }

    }

    @Nested
    class validationFails {

        @BeforeEach
        void setUp() {
            when(bindingRepository.containsInternalBindingId(HAPPY_BINDING_ID))
                    .thenReturn(true);
            when(jobRepository.getJobProgressByReferenceId(HAPPY_BINDING_ID))
                    .thenReturn(jobProgress);
            when(jobProgress.isBinding())
                    .thenReturn(true);
            when(jobProgress.isInProgress())
                    .thenReturn(false);
            when(bindingRepository.findOne(HAPPY_BINDING_ID))
                    .thenReturn(serviceInstanceBinding);
            when(serviceInstanceBinding.getServiceInstanceId())
                    .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
        }

        @Test
        void withExceptionInWouldCreateIdenticalBinding() throws ServiceInstanceDoesNotExistException {
            ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException("Mock");
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenThrow(expectedEx);
            ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                   () -> service.validateBindingNotExists(request,
                                                                                                          HAPPY_BINDING_ID,
                                                                                                          HAPPY_SERVICE_INSTANCE_ID));
            assertSame(expectedEx, ex);
        }

        @Test
        void withWouldCreateIdenticalBinding() throws ServiceInstanceDoesNotExistException {
            // Necessary mocks for wouldCreateIdenticalBinding to return true
            // Could be done with null values too, but real strings are more likely in production
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstanceBinding.getId())
                    .thenReturn(HAPPY_BINDING_ID);
            when(routeBindingRepository.containsRouteBindingId(HAPPY_BINDING_ID))
                    .thenReturn(true);
            when(routeBindingRepository.findOne(HAPPY_BINDING_ID))
                    .thenReturn(routeBinding);
            when(routeBinding.getRoute())
                    .thenReturn(HAPPY_ROUTE);
            when(request.getBindResource())
                    .thenReturn(bindResource);
            when(bindResource.getRoute())
                    .thenReturn(HAPPY_ROUTE);
            when(request.getAppGuid())
                    .thenReturn(HAPPY_APP_GUID);
            when(serviceInstanceBinding.getAppGuid())
                    .thenReturn(HAPPY_APP_GUID);
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(request.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            when(serviceInstance.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
            when(request.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
            // Mocks needed for the thrown exception and the comparison against it
            when(serviceInstanceBinding.getCredentials())
                    .thenReturn(HAPPY_CREDENTIALS);
            when(serviceInstanceBinding.getSyslogDrainUrl())
                    .thenReturn(HAPPY_SYSLOG_DRAIN_URL);

            ServiceInstanceBindingExistsException expectedEx = new ServiceInstanceBindingExistsException(HAPPY_BINDING_ID,
                                                                                                         HAPPY_SERVICE_INSTANCE_ID,
                                                                                                         true,
                                                                                                         new ServiceInstanceBindingResponse(serviceInstanceBinding));
            ServiceInstanceBindingExistsException ex = assertThrows(ServiceInstanceBindingExistsException.class,
                                                                    () -> service.validateBindingNotExists(request,
                                                                                                           HAPPY_BINDING_ID,
                                                                                                           HAPPY_SERVICE_INSTANCE_ID));
            assertEquals(expectedEx, ex);

        }

        @Nested
        class withWouldNotCreateIdenticalBinding {

            @BeforeEach
            void setUp() throws ServiceInstanceDoesNotExistException {
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(serviceInstance);
                when(serviceInstanceBinding.getId())
                        .thenReturn(HAPPY_BINDING_ID);
            }

            private void testForServiceInstanceBindingExistsException() {
                ServiceInstanceBindingExistsException expectedEx = new ServiceInstanceBindingExistsException(HAPPY_BINDING_ID,
                                                                                                             HAPPY_SERVICE_INSTANCE_ID);
                ServiceInstanceBindingExistsException ex = assertThrows(ServiceInstanceBindingExistsException.class,
                                                                        () -> service.validateBindingNotExists(request,
                                                                                                               HAPPY_BINDING_ID,
                                                                                                               HAPPY_SERVICE_INSTANCE_ID));
                assertEquals(expectedEx, ex);
            }

            @Nested
            class withRouteBindingDiffers {

                private void mocksForNotNullBindingRequestBindingRoute() {
                    when(request.getBindResource())
                            .thenReturn(bindResource);
                    when(bindResource.getRoute())
                            .thenReturn("REQUEST");
                }

                private void mocksForNotNullInstanceBindingBindingRoute() {
                    when(routeBindingRepository.containsRouteBindingId(HAPPY_BINDING_ID))
                            .thenReturn(true);
                    when(routeBindingRepository.findOne(HAPPY_BINDING_ID))
                            .thenReturn(routeBinding);
                    when(routeBinding.getRoute())
                            .thenReturn("INSTANCE");
                }

                @Nested
                class withBindingRequestRouteBindingNull {

                    @BeforeEach
                    void setUp() {
                        mocksForNotNullInstanceBindingBindingRoute();
                    }

                    @Test
                    void withBindResourceNull() {
                        when(request.getBindResource())
                                .thenReturn(null);
                        testForServiceInstanceBindingExistsException();
                    }

                    @Test
                    void withGetRouteReturnsNull() {
                        when(request.getBindResource())
                                .thenReturn(bindResource);
                        when(bindResource.getRoute())
                                .thenReturn(null);
                        testForServiceInstanceBindingExistsException();
                    }

                }

                @Nested
                class withInstanceBindingRouteBindingNull {

                    @BeforeEach
                    void setUp() {
                        mocksForNotNullBindingRequestBindingRoute();
                    }

                    @Test
                    void withContainsRouteBindingIdFalse() {
                        when(routeBindingRepository.containsRouteBindingId(HAPPY_BINDING_ID))
                                .thenReturn(false);
                        testForServiceInstanceBindingExistsException();
                    }

                    @Test
                    void withGetRouteReturnsNull() {
                        when(routeBindingRepository.containsRouteBindingId(HAPPY_BINDING_ID))
                                .thenReturn(true);
                        when(routeBindingRepository.findOne(HAPPY_BINDING_ID))
                                .thenReturn(routeBinding);
                        when(routeBinding.getRoute())
                                .thenReturn(null);
                        testForServiceInstanceBindingExistsException();
                    }

                }

                @Test
                void withRouteBindingsNotNullButDifferent() {
                    mocksForNotNullInstanceBindingBindingRoute();
                    mocksForNotNullBindingRequestBindingRoute();
                    testForServiceInstanceBindingExistsException();
                }

            }

            private void mocksForRouteBindingEqual() {
                when(routeBindingRepository.containsRouteBindingId(HAPPY_BINDING_ID))
                        .thenReturn(true);
                when(routeBindingRepository.findOne(HAPPY_BINDING_ID))
                        .thenReturn(routeBinding);
                when(routeBinding.getRoute())
                        .thenReturn(HAPPY_ROUTE);
                when(request.getBindResource())
                        .thenReturn(bindResource);
                when(bindResource.getRoute())
                        .thenReturn(HAPPY_ROUTE);
            }

            /**
             * We do not test the whole getAppGuidFromBindingRequest here,
             * as it is already done while testing bindService.
             */
            @Nested
            class withAppGuidDiffers {

                private final String HAPPY_APP_GUID_REQUEST  = "REQUEST";
                private final String HAPPY_APP_GUID_INSTANCE = "INSTANCE";

                @BeforeEach
                void setUp() {
                    mocksForRouteBindingEqual();
                }

                @Test
                void withBindingRequestAppGuidNull() {
                    when(serviceInstanceBinding.getAppGuid())
                            .thenReturn(HAPPY_APP_GUID_INSTANCE);
                    when(request.getAppGuid())
                            .thenReturn(null);
                    testForServiceInstanceBindingExistsException();
                }

                @Test
                void withInstanceBindingAppGuidNull() {
                    when(request.getAppGuid())
                            .thenReturn(HAPPY_APP_GUID_REQUEST);
                    when(serviceInstanceBinding.getAppGuid())
                            .thenReturn(null);
                    testForServiceInstanceBindingExistsException();
                }

                @Test
                void withAppGuidsNotNullButDifferent() {
                    when(serviceInstanceBinding.getAppGuid())
                            .thenReturn(HAPPY_APP_GUID_INSTANCE);
                    when(request.getAppGuid())
                            .thenReturn(HAPPY_APP_GUID_REQUEST);
                    testForServiceInstanceBindingExistsException();
                }

            }

            private void mocksForAppGuidEqual() {
                mocksForRouteBindingEqual();
                when(request.getAppGuid())
                        .thenReturn(HAPPY_APP_GUID);
                when(serviceInstanceBinding.getAppGuid())
                        .thenReturn(HAPPY_APP_GUID);
            }

            @Nested
            class withServiceDefinitionIdDiffers {

                private final String HAPPY_SERVICE_DEFINITION_ID_REQUEST     = "REQUEST";
                private final String HAPPY_SERVICE_DEFINITION_ID_INSTANCE    = "INSTANCE";

                @BeforeEach
                void setUp() {
                    mocksForAppGuidEqual();
                }

                @Test
                void withBindingRequestServiceDefinitionIdNull() {
                    when(serviceInstance.getServiceDefinitionId())
                            .thenReturn(HAPPY_SERVICE_DEFINITION_ID_INSTANCE);
                    when(request.getServiceDefinitionId())
                            .thenReturn(null);
                    testForServiceInstanceBindingExistsException();
                }

                @Test
                void withInstanceBindingServiceDefinitionIdNull() {
                    when(request.getServiceDefinitionId())
                            .thenReturn(HAPPY_SERVICE_DEFINITION_ID_REQUEST);
                    when(serviceInstance.getServiceDefinitionId())
                            .thenReturn(null);
                    testForServiceInstanceBindingExistsException();
                }

                @Test
                void withServiceDefinitionIdsNotNullButDifferent() {
                    when(serviceInstance.getServiceDefinitionId())
                            .thenReturn(HAPPY_SERVICE_DEFINITION_ID_INSTANCE);
                    when(request.getServiceDefinitionId())
                            .thenReturn(HAPPY_SERVICE_DEFINITION_ID_REQUEST);
                    testForServiceInstanceBindingExistsException();
                }

            }

            private void mocksForServiceDefinitionIdEqual() {
                mocksForAppGuidEqual();
                when(serviceInstance.getServiceDefinitionId())
                        .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
                when(request.getServiceDefinitionId())
                        .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            }

            @Nested
            class withPlanIdDiffers {

                private final String HAPPY_PLAN_ID_REQUEST   = "REQUEST";
                private final String HAPPY_PLAN_ID_INSTANCE  = "INSTANCE";

                @BeforeEach
                void setUp() {
                    mocksForServiceDefinitionIdEqual();
                }

                @Test
                void withBindingRequestPlanIdNull() {
                    when(serviceInstance.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID_INSTANCE);
                    when(request.getPlanId())
                            .thenReturn(null);
                    testForServiceInstanceBindingExistsException();
                }

                @Test
                void withInstanceBindingPlanIdNull() {
                    when(request.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID_REQUEST);
                    when(serviceInstance.getPlanId())
                            .thenReturn(null);
                    testForServiceInstanceBindingExistsException();
                }

                @Test
                void withPlanIdsNotNullButDifferent() {
                    when(serviceInstance.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID_INSTANCE);
                    when(request.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID_REQUEST);
                    testForServiceInstanceBindingExistsException();
                }

            }

            @Nested
            class withParametersDiffers {

                private final Map<String, Object> HAPPY_PARAMETERS_REQUEST  = new HashMap<>() {{
                    put("RKEY1", "RVALUE1");
                    put("RKEY2", "RVALUE2");
                }};
                private final Map<String, Object> HAPPY_PARAMETERS_INSTANCE = new HashMap<>() {{
                    put("IKEY1", "IVALUE1");
                    put("IKEY2", "IVALUE2");
                }};

                @BeforeEach
                void setUp() {
                    mocksForServiceDefinitionIdEqual();
                    when(serviceInstance.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID);
                    when(request.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID);
                }

                @Test
                void withBindingRequestPlanIdNull() {
                    when(serviceInstanceBinding.getParameters())
                            .thenReturn(HAPPY_PARAMETERS_INSTANCE);
                    when(request.getParameters())
                            .thenReturn(null);
                    testForServiceInstanceBindingExistsException();
                }

                @Test
                void withInstanceBindingPlanIdNull() {
                    when(request.getParameters())
                            .thenReturn(HAPPY_PARAMETERS_REQUEST);
                    when(serviceInstanceBinding.getParameters())
                            .thenReturn(null);
                    testForServiceInstanceBindingExistsException();
                }

                @Test
                void withPlanIdsNotNullButDifferent() {
                    when(serviceInstanceBinding.getParameters())
                            .thenReturn(HAPPY_PARAMETERS_INSTANCE);
                    when(request.getParameters())
                            .thenReturn(HAPPY_PARAMETERS_REQUEST);
                    testForServiceInstanceBindingExistsException();
                }

            }

        }

    }

}
