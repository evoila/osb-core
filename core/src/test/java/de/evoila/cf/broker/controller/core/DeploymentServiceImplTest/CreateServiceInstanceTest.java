package de.evoila.cf.broker.controller.core.DeploymentServiceImplTest;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Optional;

import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceOperationResponse;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.catalog.Dashboard;
import de.evoila.cf.broker.model.catalog.plan.SchemaServiceCreate;
import de.evoila.cf.broker.model.catalog.plan.SchemaServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Schemas;
import de.evoila.cf.broker.model.context.Context;
import de.evoila.cf.broker.model.json.schema.JsonSchema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateServiceInstanceTest extends BaseTest {

    @Mock
    ServiceInstanceRequest request;

    @BeforeEach
    void setUp() {
        super.setUp();

        when(request.getServiceDefinitionId())
                .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
    }

    @Test
    void validateServiceIdThrows() throws ServiceDefinitionDoesNotExistException {
        ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID);
        doThrow(expectedEx)
                .when(serviceDefinitionRepository)
                .validateServiceId(HAPPY_SERVICE_DEFINITION_ID);
        ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                 () -> service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                     request));
        assertSame(expectedEx, ex);
    }

    @Nested
    class validateServiceIdDoesNotThrow {

        @Nested
        class serviceInstanceAlreadyPresent {

            @BeforeEach
            void setUp() {
                when(serviceInstanceRepository.getServiceInstanceOptional(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(Optional.of(serviceInstance));
            }

            private void testForServiceInstanceExistsException() {
                ServiceInstanceExistsException expectedEx = new ServiceInstanceExistsException(HAPPY_SERVICE_INSTANCE_ID,
                                                                                               HAPPY_SERVICE_DEFINITION_ID);
                ServiceInstanceExistsException ex = assertThrows(ServiceInstanceExistsException.class,
                                                                 () -> service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                     request));
                assertEquals(expectedEx, ex);
            }

            @Test
            void getJobProgressByReferenceIdReturnsNull() {
                when(jobRepository.getJobProgressByReferenceId(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(null);
                testForServiceInstanceExistsException();
            }

            @Nested
            class getJobProgressByReferenceIdDoesNotReturnNull {

                @BeforeEach
                void setUp() {
                    when(jobRepository.getJobProgressByReferenceId(HAPPY_SERVICE_INSTANCE_ID))
                            .thenReturn(jobProgress);
                }

                @Test
                void isProvisioningReturnsFalse() {
                    when(jobProgress.isProvisioning())
                            .thenReturn(false);
                    testForServiceInstanceExistsException();
                }

                @Nested
                class isProvisioningReturnsTrue {

                    @BeforeEach
                    void setUp() {
                        when(jobProgress.isProvisioning())
                                .thenReturn(true);
                    }

                    @Test
                    void isInProgressReturnsTrue() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceExistsException {
                        when(jobProgress.isInProgress())
                                .thenReturn(true);
                        when(jobProgress.getId())
                                .thenReturn(HAPPY_JOB_PROGRESS_ID);
                        when(serviceInstance.getDashboardUrl())
                                .thenReturn(HAPPY_DASHBOARD_URL);
                        ServiceInstanceOperationResponse expectedResponse = new ServiceInstanceOperationResponse(HAPPY_JOB_PROGRESS_ID,
                                                                                                                 HAPPY_DASHBOARD_URL,
                                                                                                                 true);
                        ServiceInstanceOperationResponse response = service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                  request);
                        assertEquals(expectedResponse, response);
                    }

                    @Nested
                    class isInProgressReturnsFalse {

                        @BeforeEach
                        void setUp() {
                            when(jobProgress.isInProgress())
                                    .thenReturn(false);
                        }

                        @Test
                        void isSucceededReturnsFalse() {
                            when(jobProgress.isSucceeded())
                                    .thenReturn(false);
                            testForServiceInstanceExistsException();
                        }

                        @Nested
                        class isSucceededReturnsTrue {

                            @Mock
                            Context context;

                            @BeforeEach
                            void setUp() {
                                when(jobProgress.isSucceeded())
                                        .thenReturn(true);
                            }

                            @Test
                            void wouldCreateIdenticalInstanceReturnsFalse() {
                                when(request.getContext())
                                        .thenReturn(context);
                                when(serviceInstance.getContext())
                                        .thenReturn(null);
                                testForServiceInstanceExistsException();
                            }

                            @Test
                            void wouldCreateIdenticalInstanceReturnsTrue() {
                                when(request.getContext())
                                        .thenReturn(context);
                                when(serviceInstance.getContext())
                                        .thenReturn(context);
                                when(serviceInstance.getId())
                                        .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
                                when(request.getServiceDefinitionId())
                                        .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                                when(serviceInstance.getServiceDefinitionId())
                                        .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                                when(request.getPlanId())
                                        .thenReturn(HAPPY_PLAN_ID);
                                when(serviceInstance.getPlanId())
                                        .thenReturn(HAPPY_PLAN_ID);
                                when(request.getOrganizationGuid())
                                        .thenReturn(HAPPY_ORGANIZATION_GUID);
                                when(serviceInstance.getOrganizationGuid())
                                        .thenReturn(HAPPY_ORGANIZATION_GUID);
                                when(request.getSpaceGuid())
                                        .thenReturn(HAPPY_SPACE_GUID);
                                when(serviceInstance.getSpaceGuid())
                                        .thenReturn(HAPPY_SPACE_GUID);
                                when(request.getParameters())
                                        .thenReturn(new HashMap<>());
                                when(serviceInstance.getParameters())
                                        .thenReturn(new HashMap<>());
                                when(jobProgress.getId())
                                        .thenReturn(HAPPY_JOB_PROGRESS_ID);
                                when(serviceInstance.getDashboardUrl())
                                        .thenReturn(HAPPY_DASHBOARD_URL);
                                ServiceInstanceExistsException expectedEx = new ServiceInstanceExistsException(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                               HAPPY_SERVICE_DEFINITION_ID,
                                                                                                               true,
                                                                                                               new ServiceInstanceOperationResponse(HAPPY_JOB_PROGRESS_ID,
                                                                                                                                                    HAPPY_DASHBOARD_URL,
                                                                                                                                                    true));
                                ServiceInstanceExistsException ex = assertThrows(ServiceInstanceExistsException.class,
                                                                                 () -> service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                                     request));
                                assertEquals(expectedEx, ex);
                            }

                        }

                    }

                }

            }

        }

        @Nested
        class serviceInstanceNotPresent {

            @BeforeEach
            void setUp() {
                when(serviceInstanceRepository.getServiceInstanceOptional(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(Optional.empty());
            }

            @Test
            void getServiceDefinitionThrows() throws ServiceDefinitionDoesNotExistException {
                ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID);
                when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                        .thenThrow(expectedEx);
                ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                         () -> service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                             request));
                assertSame(expectedEx, ex);
            }

            @Nested
            class getServiceDefinitionDoesNotThrow {

                @BeforeEach
                void setUp() throws ServiceDefinitionDoesNotExistException {
                    when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                            .thenReturn(serviceDefinition);
                    when(request.getServiceDefinitionId())
                            .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                    when(request.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID);
                    when(request.getOrganizationGuid())
                            .thenReturn(HAPPY_ORGANIZATION_GUID);
                    when(request.getSpaceGuid())
                            .thenReturn(HAPPY_SPACE_GUID);
                    when(request.getParameters())
                            .thenReturn(new HashMap<>());
                    when(serviceDefinition.isAllowContextUpdates())
                            .thenReturn(true);
                }

                @Test
                void getPlanThrows() throws ServiceDefinitionDoesNotExistException {
                    ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_PLAN_ID);
                    when(serviceDefinitionRepository.getPlan(HAPPY_PLAN_ID))
                            .thenThrow(expectedEx);
                    ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                             () -> service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
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
                        when(schemas.getServiceInstance())
                                .thenReturn(schemaServiceInstance);
                        when(schemaServiceInstance.getCreate())
                                .thenReturn(schemaServiceCreate);
                        when(schemaServiceCreate.getParameters())
                                .thenReturn(jsonSchema);
                        ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                                 () -> service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
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
                            when(platformRepository.getPlatformService(HAPPY_PLATFORM))
                                    .thenReturn(null);
                            ServiceBrokerException expectedEx = new ServiceBrokerException("No Platform configured for " + plan.getPlatform());
                            ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                                     () -> service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                         request));
                            assertEquals(expectedEx, ex);
                        }

                        @Nested
                        class getPlatformServiceDoesNotReturnNull {

                            @BeforeEach
                            void setUp() {
                                when(platformRepository.getPlatformService(HAPPY_PLATFORM))
                                        .thenReturn(platformService);
                            }

                            @Test
                            void isSyncPossibleOnCreateReturnsTrue() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceExistsException {
                                when(platformService.isSyncPossibleOnCreate(plan))
                                        .thenReturn(true);
                                ServiceInstanceOperationResponse expectedResponse = new ServiceInstanceOperationResponse();
                                ServiceInstanceOperationResponse response = service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                          request);
                                assertEquals(expectedResponse, response);
                            }

                            @Nested
                            class isSyncPossibleOnCreateReturnsFalse {

                                @Mock
                                private Dashboard dashboard;
                                @Mock
                                private DashboardClient dashboardClient;

                                private ServiceInstance expectedServiceInstance;
                                private ServiceInstanceOperationResponse expectedResponse;

                                private void validateResponse(ServiceInstanceOperationResponse response) {
                                    verify(serviceInstanceRepository)
                                            .saveServiceInstance(expectedServiceInstance);
                                    verify(asyncDeploymentService)
                                            .asyncCreateInstance(service,
                                                                 expectedServiceInstance,
                                                                 request.getParameters(),
                                                                 plan,
                                                                 platformService,
                                                                 HAPPY_JOB_PROGRESS_ID);
                                    assertEquals(expectedResponse, response);
                                }

                                @BeforeEach
                                void setUp() {
                                    when(platformService.isSyncPossibleOnCreate(plan))
                                            .thenReturn(false);
                                    when(randomString.nextString())
                                            .thenReturn(HAPPY_JOB_PROGRESS_ID);
                                    expectedServiceInstance = new ServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                  HAPPY_SERVICE_DEFINITION_ID,
                                                                                  HAPPY_PLAN_ID,
                                                                                  HAPPY_ORGANIZATION_GUID,
                                                                                  HAPPY_SPACE_GUID,
                                                                                  request.getParameters(),
                                                                                  request.getContext());
                                    expectedServiceInstance.setAllowContextUpdates(serviceDefinition.isAllowContextUpdates());
                                    expectedResponse = new ServiceInstanceOperationResponse();
                                    expectedResponse.setOperation(HAPPY_JOB_PROGRESS_ID);
                                    expectedResponse.setAsync(true);
                                }

                                @Test
                                void noDashboardUrlSet() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceExistsException {
                                    ServiceInstanceOperationResponse response = service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                              request);
                                    validateResponse(response);
                                }

                                @Test
                                void dashboardUrlSet() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceExistsException {
                                    when(serviceDefinition.getDashboard())
                                            .thenReturn(dashboard);
                                    when(dashboard.getUrl())
                                            .thenReturn(HAPPY_DASHBOARD_URL);
                                    when(serviceDefinition.getDashboardClient())
                                            .thenReturn(dashboardClient);
                                    String expectedDashboardUrl = DashboardUtils.dashboard(serviceDefinition, HAPPY_SERVICE_INSTANCE_ID);
                                    expectedServiceInstance.setDashboardUrl(expectedDashboardUrl);
                                    expectedResponse.setDashboardUrl(expectedDashboardUrl);
                                    ServiceInstanceOperationResponse response = service.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                              request);
                                    validateResponse(response);
                                }

                            }

                        }

                    }

                }

            }

        }

    }

}
