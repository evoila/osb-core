package de.evoila.cf.broker.service.impl.DeploymentServiceImplTest;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Map;

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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
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
                            service = spy(service);
                            when(platformService.isSyncPossibleOnUpdate(serviceInstance, plan))
                                    .thenReturn(true);
                        }

                        @Test
                        void syncUpdateInstanceThrows() throws ServiceBrokerException {
                            PlatformException platformEx = new PlatformException("Mock");
                            ServiceBrokerException expectedEx = new ServiceBrokerException("Mock", platformEx);
                            // We have to save the parameters in a local variable, otherwise mockito
                            // is failing with an unfished stubbing error
                            Map<String, Object> requestParameters = request.getParameters();
                            doThrow(expectedEx)
                                    .when(service)
                                    .syncUpdateInstance(serviceInstance,
                                                        requestParameters,
                                                        plan,
                                                        platformService);
                            ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                                     () -> service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                         request));
                            assertEquals(expectedEx, ex);
                            assertSame(platformEx, ex.getCause());
                        }

                        @Test
                        void syncUpdateInstanceDoesNotThrow() throws ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
                            // We have to save the parameters in a local variable, otherwise mockito
                            // is failing with an unfished stubbing error
                            Map<String, Object> requestParameters = request.getParameters();
                            doReturn(null)
                                    .when(service)
                                    .syncUpdateInstance(serviceInstance,
                                                        requestParameters,
                                                        plan,
                                                        platformService);
                            ServiceInstanceOperationResponse response = service.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                      request);
                            verify(service, times(1))
                                    .syncUpdateInstance(serviceInstance,
                                                        request.getParameters(),
                                                        plan,
                                                        platformService);
                            assertEquals(expectedResponse, response);
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
