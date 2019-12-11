package de.evoila.cf.broker.controller.core.ServiceInstanceControllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Map;

import de.evoila.cf.broker.exception.AsyncRequiredException;
import de.evoila.cf.broker.exception.MaintenanceInfoVersionsDontMatchException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceDefinitionPlanDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceNotFoundException;
import de.evoila.cf.broker.model.ServiceBrokerErrorResponse;
import de.evoila.cf.broker.model.ServiceInstanceOperationResponse;
import de.evoila.cf.broker.model.ServiceInstanceUpdateRequest;
import de.evoila.cf.broker.model.context.Context;
import de.evoila.cf.broker.util.EmptyRestResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UpdateTest extends BaseTest {

    @Mock
    ServiceInstanceUpdateRequest request;

    @BeforeEach
    void setUp() {
        super.setUp();

        when(request.getServiceDefinitionId())
                .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getServiceDefinitionIdReturnsNull() throws AsyncRequiredException, ServiceDefinitionPlanDoesNotExistException, ServiceInstanceNotFoundException, ServiceBrokerException, MaintenanceInfoVersionsDontMatchException, ServiceDefinitionDoesNotExistException {
        when(request.getServiceDefinitionId())
                .thenReturn(null);
        ResponseEntity<String> response = controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                            HAPPY_ACCEPTS_INCOMPLETE,
                                                            request,
                                                            HAPPY_ORIGINATING_ID,
                                                            HAPPY_REQUEST_ID);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing required fields: service_id", response.getBody());
    }

    @Nested
    class checkMaintenanceInfoSucceeds {

        @BeforeEach
        void setUp() throws ServiceDefinitionDoesNotExistException {
            when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                    .thenReturn(serviceDefinition);
            when(serviceDefinition.getPlans())
                    .thenReturn(new ArrayList<>() {{
                        add(plan);
                    }});
            when(request.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
            when(plan.getId())
                    .thenReturn(HAPPY_PLAN_ID);
            when(request.getMaintenanceInfo())
                    .thenReturn(requestMaintenanceInfo);
            when(plan.getMaintenanceInfo())
                    .thenReturn(planMaintenanceInfo);
            when(requestMaintenanceInfo.getVersion())
                    .thenReturn(HAPPY_MAINTENANCE_INFO_VERSION);
            when(planMaintenanceInfo.getVersion())
                    .thenReturn(HAPPY_MAINTENANCE_INFO_VERSION);
        }

        @Test
        void acceptsIncompleteFalse() {
            assertThrows(AsyncRequiredException.class,
                         () -> controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                 false,
                                                 request,
                                                 HAPPY_ORIGINATING_ID,
                                                 HAPPY_REQUEST_ID));
        }

        @Test
        void acceptsIncompleteNull() {
            assertThrows(AsyncRequiredException.class,
                         () -> controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                 null,
                                                 request,
                                                 HAPPY_ORIGINATING_ID,
                                                 HAPPY_REQUEST_ID));
        }

        @Test
        void getServiceDefinitionThrows() throws ServiceInstanceDoesNotExistException {
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenThrow(new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID));
            assertThrows(ServiceInstanceNotFoundException.class,
                         () -> controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                 HAPPY_ACCEPTS_INCOMPLETE,
                                                 request,
                                                 HAPPY_ORIGINATING_ID,
                                                 HAPPY_REQUEST_ID));
        }

        @Test
        void specificPlanIsUpdatableThrows() throws ServiceInstanceDoesNotExistException, ServiceDefinitionPlanDoesNotExistException {
            ServiceDefinitionPlanDoesNotExistException expectedEx = new ServiceDefinitionPlanDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID, HAPPY_PLAN_ID);
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
            when(serviceDefinition.specificPlanIsUpdatable(HAPPY_PLAN_ID))
                    .thenThrow(expectedEx);
            ServiceDefinitionPlanDoesNotExistException ex = assertThrows(ServiceDefinitionPlanDoesNotExistException.class,
                                                                         () -> controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                 HAPPY_ACCEPTS_INCOMPLETE,
                                                                                                 request,
                                                                                                 HAPPY_ORIGINATING_ID,
                                                                                                 HAPPY_REQUEST_ID));
            assertSame(expectedEx, ex);
        }

        @SuppressWarnings("unchecked")
        @Test
        void specificPlanIsUpdatableReturnsFalse() throws ServiceInstanceDoesNotExistException, ServiceDefinitionPlanDoesNotExistException, AsyncRequiredException, ServiceInstanceNotFoundException, ServiceBrokerException, ServiceDefinitionDoesNotExistException, MaintenanceInfoVersionsDontMatchException {
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
            when(serviceDefinition.specificPlanIsUpdatable(HAPPY_PLAN_ID))
                    .thenReturn(false);
            ServiceBrokerErrorResponse expectedResponse = new ServiceBrokerErrorResponse("NotUpdatable",
                                                                                         "An update on the requested service instance is not supported.");
            ResponseEntity<ServiceBrokerErrorResponse> response = controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                                                    HAPPY_ACCEPTS_INCOMPLETE,
                                                                                    request,
                                                                                    HAPPY_ORIGINATING_ID,
                                                                                    HAPPY_REQUEST_ID);
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertEquals(expectedResponse, response.getBody());
        }

        @Nested
        class specificPlanIsUpdatableReturnsTrue {

            @Mock
            Context context;
            @Mock
            Map<String, Object> parameters;

            @BeforeEach
            void setUp() throws ServiceInstanceDoesNotExistException, ServiceDefinitionPlanDoesNotExistException {
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(serviceInstance);
                when(serviceInstance.getPlanId())
                        .thenReturn(HAPPY_PLAN_ID);
                when(serviceDefinition.specificPlanIsUpdatable(HAPPY_PLAN_ID))
                        .thenReturn(true);
            }

            @SuppressWarnings("unchecked")
            @Test
            void isEffectivelyUpdatingReturnsFalse() throws AsyncRequiredException, ServiceDefinitionPlanDoesNotExistException, ServiceInstanceNotFoundException, ServiceBrokerException, MaintenanceInfoVersionsDontMatchException, ServiceDefinitionDoesNotExistException {
                when(request.getContext())
                        .thenReturn(context);
                when(serviceInstance.getContext())
                        .thenReturn(context);
                when(request.getParameters())
                        .thenReturn(parameters);
                when(serviceInstance.getParameters())
                        .thenReturn(parameters);
                when(serviceInstance.getServiceDefinitionId())
                        .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                ResponseEntity<String> response = controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                                    HAPPY_ACCEPTS_INCOMPLETE,
                                                                    request,
                                                                    HAPPY_ORIGINATING_ID,
                                                                    HAPPY_REQUEST_ID);
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(EmptyRestResponse.BODY, response.getBody());

            }

            @Nested
            class isEffectivelyUpdatingReturnsTrue {

                @BeforeEach
                void setUp() {
                    when(request.getContext())
                            .thenReturn(context);
                    when(serviceInstance.getContext())
                            .thenReturn(null);
                }

                @Nested
                class isContextUpdateReturnsTrue {

                    @BeforeEach
                    void setUp() {
                        when(request.isContextUpdate())
                                .thenReturn(true);
                    }

                    @Nested
                    class isAllowContextUpdatesReturnsTrue {

                        @BeforeEach
                        void setUp() {
                            when(serviceInstance.isAllowContextUpdates())
                                    .thenReturn(true);
                        }

                        @Nested
                        class updateServiceInstanceContextThrows {

                            @Test
                            void caught() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
                                when(deploymentService.updateServiceInstanceContext(HAPPY_SERVICE_INSTANCE_ID, request))
                                        .thenThrow(new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID));
                                assertThrows(ServiceInstanceNotFoundException.class,
                                             () -> controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                                     HAPPY_ACCEPTS_INCOMPLETE,
                                                                     request,
                                                                     HAPPY_ORIGINATING_ID,
                                                                     HAPPY_REQUEST_ID));
                            }

                            @Test
                            void notCaught() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
                                Exception[] exceptions = {
                                        new ServiceBrokerException(),
                                        new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID)
                                };
                                when(deploymentService.updateServiceInstanceContext(HAPPY_SERVICE_INSTANCE_ID, request))
                                        .thenThrow(exceptions);
                                for (Exception expectedEx : exceptions) {
                                    Exception ex = assertThrows(expectedEx.getClass(),
                                                                () -> controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                                                        HAPPY_ACCEPTS_INCOMPLETE,
                                                                                        request,
                                                                                        HAPPY_ORIGINATING_ID,
                                                                                        HAPPY_REQUEST_ID));
                                    assertSame(expectedEx, ex);
                                }
                            }

                        }

                        @SuppressWarnings("unchecked")
                        @Test
                        void updateServiceInstanceContextDoesNotThrow() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException, AsyncRequiredException, ServiceInstanceNotFoundException, ServiceDefinitionPlanDoesNotExistException, MaintenanceInfoVersionsDontMatchException {
                            when(serviceInstance.isAllowContextUpdates())
                                    .thenReturn(true);
                            when(deploymentService.updateServiceInstanceContext(HAPPY_SERVICE_INSTANCE_ID, request))
                                    .thenReturn(operationResponse);
                            ResponseEntity<ServiceInstanceOperationResponse> response = controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                          HAPPY_ACCEPTS_INCOMPLETE,
                                                                                                          request,
                                                                                                          HAPPY_ORIGINATING_ID,
                                                                                                          HAPPY_REQUEST_ID);
                            assertEquals(HttpStatus.OK, response.getStatusCode());
                            assertSame(operationResponse, response.getBody());
                        }

                    }

                    @SuppressWarnings("unchecked")
                    @Test
                    void isAllowContextUpdatesReturnsFalse() throws AsyncRequiredException, ServiceDefinitionPlanDoesNotExistException, ServiceInstanceNotFoundException, ServiceBrokerException, MaintenanceInfoVersionsDontMatchException, ServiceDefinitionDoesNotExistException {
                        when(serviceInstance.isAllowContextUpdates())
                                .thenReturn(false);
                        ServiceBrokerErrorResponse expectedResponse = new ServiceBrokerErrorResponse("ContextUpdateNotAllowed",
                                                                                                     "It is not allowed to alter the context of the requested service instance");
                        ResponseEntity<ServiceBrokerErrorResponse> response = controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                HAPPY_ACCEPTS_INCOMPLETE,
                                                                                                request,
                                                                                                HAPPY_ORIGINATING_ID,
                                                                                                HAPPY_REQUEST_ID);
                        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
                        assertEquals(expectedResponse, response.getBody());
                    }

                }

                @Nested
                class isContextUpdateReturnsFalse {

                    @BeforeEach
                    void setUp() {
                        when(request.isContextUpdate())
                                .thenReturn(false);
                    }

                    @Nested
                    class updateServiceInstanceThrows {

                        @Test
                        void caught() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
                            when(deploymentService.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID, request))
                                    .thenThrow(new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID));
                            assertThrows(ServiceInstanceNotFoundException.class,
                                         () -> controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                                 HAPPY_ACCEPTS_INCOMPLETE,
                                                                 request,
                                                                 HAPPY_ORIGINATING_ID,
                                                                 HAPPY_REQUEST_ID));
                        }

                        @Test
                        void notCaught() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
                            Exception[] exceptions = {
                                    new ServiceBrokerException(),
                                    new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID)
                            };
                            when(deploymentService.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID, request))
                                    .thenThrow(exceptions);
                            for (Exception expectedEx : exceptions) {
                                Exception ex = assertThrows(expectedEx.getClass(),
                                                            () -> controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                                                    HAPPY_ACCEPTS_INCOMPLETE,
                                                                                    request,
                                                                                    HAPPY_ORIGINATING_ID,
                                                                                    HAPPY_REQUEST_ID));
                                assertSame(expectedEx, ex);
                            }
                        }

                    }

                    @SuppressWarnings("unchecked")
                    @Test
                    void updateServiceInstanceDoesNotThrow() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceDoesNotExistException, AsyncRequiredException, ServiceInstanceNotFoundException, ServiceDefinitionPlanDoesNotExistException, MaintenanceInfoVersionsDontMatchException {
                        when(deploymentService.updateServiceInstance(HAPPY_SERVICE_INSTANCE_ID, request))
                                .thenReturn(operationResponse);
                        ResponseEntity<ServiceInstanceOperationResponse> response = controller.update(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                      HAPPY_ACCEPTS_INCOMPLETE,
                                                                                                      request,
                                                                                                      HAPPY_ORIGINATING_ID,
                                                                                                      HAPPY_REQUEST_ID);
                        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
                        assertSame(operationResponse, response.getBody());
                    }

                }

            }

        }

    }

    /**
     * We do not test checkMaintenanceInfo here as it is already
     * completely tested in {@link CreateTest}.
     * Therefore we mock all calls to make sure that
     * checkMaintenanceInfo always succeeds.
     */
    @SuppressWarnings("InnerClassMayBeStatic")
    @Nested
    class checkMaintenanceInfoThrows {
    }

}
