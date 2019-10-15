package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import de.evoila.cf.broker.exception.AsyncRequiredException;
import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingOperationResponse;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.catalog.plan.SchemaServiceBinding;
import de.evoila.cf.broker.model.catalog.plan.SchemaServiceCreate;
import de.evoila.cf.broker.model.catalog.plan.Schemas;
import de.evoila.cf.broker.model.json.schema.JsonSchema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateServiceInstanceBindingTest extends BaseTest {

    @Test
    void validateBindingNotExistsThrows() throws ServiceInstanceBindingExistsException, ServiceInstanceDoesNotExistException {
        Exception[] exceptions = {
                new ServiceInstanceBindingExistsException("Mock", "Mock"),
                new ServiceInstanceDoesNotExistException("Mock")
        };
        doThrow(exceptions)
                .when(service)
                .validateBindingNotExists(any(), any(), any());
        for (Exception expectedEx : exceptions) {
            Exception ex = assertThrows(expectedEx.getClass(),
                                        () -> service.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                   HAPPY_SERVICE_INSTANCE_ID,
                                                                                   request,
                                                                                   HAPPY_ASYNC));
            assertSame(expectedEx, ex);
        }
    }

    @Nested
    class validateBindingNotExistsDoesNotThrow {

        @BeforeEach
        void setUp() throws ServiceInstanceBindingExistsException, ServiceInstanceDoesNotExistException {
            doNothing()
                    .when(service)
                    .validateBindingNotExists(request,
                                              HAPPY_BINDING_ID,
                                              HAPPY_SERVICE_INSTANCE_ID);
        }

        @Test
        void getServiceInstanceThrows() throws ServiceInstanceDoesNotExistException {
            ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException("Mock");
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenThrow(expectedEx);
            ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                   () -> service.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                                              HAPPY_SERVICE_INSTANCE_ID,
                                                                                                              request,
                                                                                                              HAPPY_ASYNC));
            assertSame(expectedEx, ex);
        }

        @Nested
        class getServiceInstanceDoesNotThrow {

            @BeforeEach
            void setUp() throws ServiceInstanceDoesNotExistException {
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(serviceInstance);
                when(request.getPlanId())
                        .thenReturn(HAPPY_PLAN_ID);
            }

            @Test
            void getPlanThrows() throws ServiceDefinitionDoesNotExistException {
                ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException("Mock");
                when(serviceDefinitionRepository.getPlan(HAPPY_PLAN_ID))
                        .thenThrow(expectedEx);
                ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                         () -> service.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                                                    HAPPY_SERVICE_INSTANCE_ID,
                                                                                                                    request,
                                                                                                                    HAPPY_ASYNC));
                assertSame(expectedEx, ex);
            }

            @Nested
            class getPlanDoesNotThrow {

                @Mock
                private Schemas schemas;
                @Mock
                private SchemaServiceBinding schemaServiceBinding;
                @Mock
                private SchemaServiceCreate schemaServiceCreate;
                @Mock
                private JsonSchema jsonSchema;

                @BeforeEach
                void setUp() throws ServiceDefinitionDoesNotExistException {
                    when(serviceDefinitionRepository.getPlan(HAPPY_PLAN_ID))
                            .thenReturn(plan);
                }

                @Test
                void validateParametersThrows() {
                    // For simplicity we only test for ServiceBrokerException here, as we cannot mock the validateParameters method itself
                    when(plan.getSchemas())
                            .thenReturn(schemas);
                    when(schemas.getServiceBinding())
                            .thenReturn(schemaServiceBinding);
                    when(schemaServiceBinding.getCreate())
                            .thenReturn(schemaServiceCreate);
                    when(schemaServiceCreate.getParameters())
                            .thenReturn(jsonSchema);
                    ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                             () -> service.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                                        HAPPY_SERVICE_INSTANCE_ID,
                                                                                                        request,
                                                                                                        HAPPY_ASYNC));
                    assertTrue(ex.getCause() instanceof JsonProcessingException);
                }

                @Nested
                class validateParametersDoesNotThrow {

                    @BeforeEach
                    void setUp() {
                        when(plan.getPlatform())
                                .thenReturn(HAPPY_PLATFORM);
                    }

                    @Test
                    void getPlatformServiceReturnsNull() {
                        when(platformRepository.getPlatformService(HAPPY_PLATFORM))
                                .thenReturn(null);
                        ServiceBrokerException expectedEx = new ServiceBrokerException("No Platform configured for " + plan.getPlatform());
                        ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                                 () -> service.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                                            HAPPY_SERVICE_INSTANCE_ID,
                                                                                                            request,
                                                                                                            HAPPY_ASYNC));
                        assertEquals(expectedEx, ex);
                    }

                    @Nested
                    class getPlatformServiceDoesNotReturnNull {

                        @BeforeEach
                        void setUp() {
                            when(platformRepository.getPlatformService(HAPPY_PLATFORM))
                                    .thenReturn(platformService);
                            when(randomString.nextString())
                                    .thenReturn(HAPPY_OPERATION_ID);
                        }

                        @Nested
                        class isSyncPossibleOnBindReturnsTrue {

                            @BeforeEach
                            void setUp() {
                                when(platformService.isSyncPossibleOnBind())
                                        .thenReturn(true);
                            }

                            @Test
                            void syncCreateBindingThrows() throws ServiceBrokerException, InvalidParametersException, PlatformException {
                                Exception[] exceptions = {
                                        new ServiceBrokerException(),
                                        new InvalidParametersException("Mock"),
                                        new PlatformException("Mock")
                                };
                                doThrow(exceptions)
                                        .when(service)
                                        .syncCreateBinding(HAPPY_BINDING_ID,
                                                           serviceInstance,
                                                           request,
                                                           plan);
                                for (Exception expectedEx : exceptions) {
                                    Exception ex = assertThrows(expectedEx.getClass(),
                                                                () -> service.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                                           HAPPY_SERVICE_INSTANCE_ID,
                                                                                                           request,
                                                                                                           HAPPY_ASYNC));
                                    assertSame(expectedEx, ex);
                                }
                            }

                            @Test
                            void syncCreateBindingDoesNotThrow() throws ServiceBrokerException, InvalidParametersException, PlatformException, ServiceInstanceBindingExistsException, AsyncRequiredException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
                                ServiceInstanceBindingResponse expectedResponse = new ServiceInstanceBindingResponse();
                                doReturn(expectedResponse)
                                        .when(service)
                                        .syncCreateBinding(HAPPY_BINDING_ID,
                                                           serviceInstance,
                                                           request,
                                                           plan);
                                ServiceInstanceBindingResponse response = (ServiceInstanceBindingResponse) service.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                                                                                HAPPY_SERVICE_INSTANCE_ID,
                                                                                                                                                request,
                                                                                                                                                HAPPY_ASYNC);
                                assertSame(expectedResponse, response);
                            }

                        }

                        @Nested
                        class isSyncPossibleOnBindReturnsFalse {

                            @BeforeEach
                            void setUp() {
                                when(platformService.isSyncPossibleOnBind())
                                        .thenReturn(false);
                            }

                            @Test
                            void asyncIsFalse() {
                                assertThrows(AsyncRequiredException.class,
                                             () -> service.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                        HAPPY_SERVICE_INSTANCE_ID,
                                                                                        request,
                                                                                        false));
                            }

                            @Test
                            void asyncIsTrue() throws AsyncRequiredException, PlatformException, ServiceInstanceBindingExistsException, ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, InvalidParametersException {
                                ServiceInstanceBinding expectedBinding = new ServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                                    HAPPY_SERVICE_INSTANCE_ID,
                                                                                                    null);
                                ServiceInstanceBindingOperationResponse expectedResponse = new ServiceInstanceBindingOperationResponse(HAPPY_OPERATION_ID);
                                ServiceInstanceBindingOperationResponse response = (ServiceInstanceBindingOperationResponse) service.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                                                                                                  HAPPY_SERVICE_INSTANCE_ID,
                                                                                                                                                                  request,
                                                                                                                                                                  HAPPY_ASYNC);
                                verify(bindingRepository, times(1))
                                        .addInternalBinding(expectedBinding);
                                verify(asyncBindingService, times(1))
                                        .asyncCreateServiceInstanceBinding(service,
                                                                           HAPPY_BINDING_ID,
                                                                           serviceInstance,
                                                                           request,
                                                                           plan,
                                                                           true,
                                                                           HAPPY_OPERATION_ID);
                                assertEquals(expectedResponse, response);
                            }

                        }

                    }

                }

            }

        }

    }

}
