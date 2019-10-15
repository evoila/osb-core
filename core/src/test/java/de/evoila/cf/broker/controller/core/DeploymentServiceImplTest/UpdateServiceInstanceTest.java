package de.evoila.cf.broker.controller.core.DeploymentServiceImplTest;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstanceOperationResponse;
import de.evoila.cf.broker.model.ServiceInstanceUpdateRequest;
import de.evoila.cf.broker.model.catalog.plan.SchemaServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.SchemaServiceUpdate;
import de.evoila.cf.broker.model.catalog.plan.Schemas;
import de.evoila.cf.broker.model.json.schema.JsonSchema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateServiceInstanceTest extends BaseTest {

    @Mock
    private ServiceInstanceUpdateRequest request;

    @Test
    void getServiceInstanceThrows() throws ServiceInstanceDoesNotExistException {
        ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID);
        when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenThrow(expectedEx);
        ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                               () -> service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                   request));
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
            ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_PLAN_ID);
            when(serviceDefinitionRepository.getPlan(HAPPY_PLAN_ID))
                    .thenThrow(expectedEx);
            ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                     () -> service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                         request));
            assertSame(expectedEx, ex);
        }

        @Nested
        class getPlanDoesNotThrow {

            @Mock
            private Schemas schemas;
            @Mock
            private SchemaServiceInstance schemaServiceInstance;
            @Mock
            private SchemaServiceUpdate schemaServiceUpdate;
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
                when(schemas.getServiceInstance())
                        .thenReturn(schemaServiceInstance);
                when(schemaServiceInstance.getUpdate())
                        .thenReturn(schemaServiceUpdate);
                when(schemaServiceUpdate.getParameters())
                        .thenReturn(jsonSchema);
                ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                         () -> service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                             request));
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
                    ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID);
                    when(platformRepository.getPlatformService(HAPPY_PLATFORM))
                            .thenReturn(null);
                    when(request.getServiceDefinitionId())
                            .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                    ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                             () -> service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                                 request));
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
                    class isSyncPossibleOnUpdateReturnsTrue {

                        @BeforeEach
                        void setUp() {
                            when(platformService.isSyncPossibleOnUpdate(serviceInstance, plan))
                                    .thenReturn(true);
                        }

                        @Test
                        void preUpdateInstanceThrows() throws PlatformException {
                            PlatformException platformEx = new PlatformException("Mock");
                            when(platformService.preUpdateInstance(serviceInstance, plan))
                                    .thenThrow(platformEx);
                            ServiceBrokerException expectedEx = new ServiceBrokerException("Error during pre service instance update", platformEx);
                            ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                                     () -> service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                         request));
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
                                when(platformService.updateInstance(serviceInstance, plan, request.getParameters()))
                                        .thenThrow(platformEx);
                                ServiceBrokerException expectedEx = new ServiceBrokerException("Could not update instance due to: ", platformEx);
                                ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                                         () -> service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                             request));
                                assertEquals(expectedEx, ex);
                                assertSame(platformEx, ex.getCause());
                            }

                            @Nested
                            class updateInstanceDoesNotThrow {

                                @BeforeEach
                                void setUp() throws PlatformException {
                                    when(platformService.updateInstance(serviceInstance, plan, request.getParameters()))
                                            .thenReturn(serviceInstance);
                                }

                                @Test
                                void postUpdateInstanceThrows() throws PlatformException {
                                    PlatformException platformEx = new PlatformException("Mock");
                                    when(platformService.postUpdateInstance(serviceInstance, plan))
                                            .thenThrow(platformEx);
                                    ServiceBrokerException expectedEx = new ServiceBrokerException("Error during post service instance update", platformEx);
                                    ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                                             () -> service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                                 request));
                                    assertEquals(expectedEx, ex);
                                    assertSame(platformEx, ex.getCause());
                                }

                                @Test
                                void postUpdateInstanceDoesNotThrow() throws PlatformException, ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
                                    when(platformService.postUpdateInstance(serviceInstance, plan))
                                            .thenReturn(serviceInstance);
                                    ServiceInstanceOperationResponse response = service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                              request);
                                    verify(serviceInstanceRepository, times(1))
                                            .updateServiceInstance(serviceInstance);
                                    assertEquals(expectedResponse, response);
                                }

                            }

                        }

                    }

                    @Test
                    void isSyncPossibleOnUpdateReturnsFalse() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
                        when(platformService.isSyncPossibleOnUpdate(serviceInstance, plan))
                                .thenReturn(false);
                        when(randomString.nextString())
                                .thenReturn(HAPPY_JOB_PROGRESS_ID);
                        expectedResponse.setOperation(HAPPY_JOB_PROGRESS_ID);
                        expectedResponse.setAsync(true);
                        ServiceInstanceOperationResponse response = service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                  request);
                        verify(asyncDeploymentService, times(1))
                                .asyncUpdateInstance(service,
                                                     serviceInstance,
                                                     request.getParameters(),
                                                     plan,
                                                     platformService,
                                                     HAPPY_JOB_PROGRESS_ID);
                        assertEquals(expectedResponse, response);
                    }

                }

            }

        }

    }

}
