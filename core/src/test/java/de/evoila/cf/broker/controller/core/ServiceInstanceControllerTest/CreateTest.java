package de.evoila.cf.broker.controller.core.ServiceInstanceControllerTest;

import de.evoila.cf.broker.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import de.evoila.cf.broker.model.ServiceInstanceOperationResponse;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class CreateTest extends BaseTest {

    @Mock
    private ServiceInstanceRequest request;

    @Test
    void acceptsIncompleteFalse() {
        assertThrows(AsyncRequiredException.class,
                     () -> controller.create(HAPPY_SERVICE_INSTANCE_ID,
                                             false,
                                             request,
                                             HAPPY_ORIGINATING_ID,
                                             HAPPY_REQUEST_ID));
    }

    @Test
    void getServiceDefinitionThrows() throws ServiceDefinitionDoesNotExistException {
        ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID);
        when(request.getServiceDefinitionId())
                .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
        when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                .thenThrow(expectedEx);
        ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                 () -> controller.create(HAPPY_SERVICE_INSTANCE_ID,
                                                                                         HAPPY_ACCEPTS_INCOMPLETE,
                                                                                         request,
                                                                                         HAPPY_ORIGINATING_ID,
                                                                                         HAPPY_REQUEST_ID));
        assertSame(expectedEx, ex);
    }

    @Test
    void getServiceDefinitionReturnsNull() throws ServiceDefinitionDoesNotExistException {
        ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID);
        when(request.getServiceDefinitionId())
                .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
        when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                .thenReturn(null);
        ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                 () -> controller.create(HAPPY_SERVICE_INSTANCE_ID,
                                                                                         HAPPY_ACCEPTS_INCOMPLETE,
                                                                                         request,
                                                                                         HAPPY_ORIGINATING_ID,
                                                                                         HAPPY_REQUEST_ID));
        assertEquals(expectedEx, ex);
    }

    @Nested
    class checkMaintenanceInfoThrows {

        @BeforeEach
        void setUp() throws ServiceDefinitionDoesNotExistException {
            when(request.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                    .thenReturn(serviceDefinition);
        }

        @Nested
        class maintenanceInfoVersionsDontMatchException {

            @BeforeEach
            void setUp() {
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
            }

            @Test
            void planInfoNull() {
                when(plan.getMaintenanceInfo())
                        .thenReturn(null);
                when(requestMaintenanceInfo.getVersion())
                        .thenReturn(HAPPY_MAINTENANCE_INFO_VERSION);
                MaintenanceInfoVersionsDontMatchException expectedEx = new MaintenanceInfoVersionsDontMatchException(requestMaintenanceInfo, null);
                MaintenanceInfoVersionsDontMatchException ex = assertThrows(MaintenanceInfoVersionsDontMatchException.class,
                                                                            () -> controller.create(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                    HAPPY_ACCEPTS_INCOMPLETE,
                                                                                                    request,
                                                                                                    HAPPY_ORIGINATING_ID,
                                                                                                    HAPPY_REQUEST_ID));
                assertEquals(expectedEx, ex);
            }

            @Test
            void versionMismatch() {
                when(plan.getMaintenanceInfo())
                        .thenReturn(planMaintenanceInfo);
                when(requestMaintenanceInfo.getVersion())
                        .thenReturn(HAPPY_MAINTENANCE_INFO_VERSION);
                when(planMaintenanceInfo.getVersion())
                        .thenReturn(null);
                MaintenanceInfoVersionsDontMatchException expectedEx = new MaintenanceInfoVersionsDontMatchException(requestMaintenanceInfo, planMaintenanceInfo);
                MaintenanceInfoVersionsDontMatchException ex = assertThrows(MaintenanceInfoVersionsDontMatchException.class,
                                                                            () -> controller.create(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                    HAPPY_ACCEPTS_INCOMPLETE,
                                                                                                    request,
                                                                                                    HAPPY_ORIGINATING_ID,
                                                                                                    HAPPY_REQUEST_ID));
                assertEquals(expectedEx, ex);
            }


        }
        @Test
        void serviceDefinitionDoesNotExistException() throws ServiceDefinitionDoesNotExistException {
            ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID);
            when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                    .thenAnswer(new Answer<ServiceDefinition>() {

                        private int count = 0;

                        @Override
                        public ServiceDefinition answer(InvocationOnMock invocationOnMock) throws Throwable {
                            if (count > 0) {
                                throw expectedEx;
                            }
                            ++count;
                            return serviceDefinition;
                        }

                    });
            ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                     () -> controller.create(HAPPY_SERVICE_INSTANCE_ID,
                                                                                             HAPPY_ACCEPTS_INCOMPLETE,
                                                                                             request,
                                                                                             HAPPY_ORIGINATING_ID,
                                                                                             HAPPY_REQUEST_ID));
            assertSame(expectedEx, ex);
        }


    }
    private void setupMocksForSuccessfulMaintenanceInfoCheck() throws ServiceDefinitionDoesNotExistException {
        when(request.getServiceDefinitionId())
                .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
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
    void createServiceInstanceThrows() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceExistsException, ServiceDefinitionPlanDoesNotExistException {
        setupMocksForSuccessfulMaintenanceInfoCheck();
        Exception[] exceptions = {
                new ServiceInstanceExistsException(HAPPY_SERVICE_INSTANCE_ID, HAPPY_SERVICE_DEFINITION_ID),
                new ServiceBrokerException(),
                new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID)
        };
        when(deploymentService.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID, request))
                .thenThrow(exceptions);
        for (Exception expectedEx : exceptions) {
            Exception ex = assertThrows(expectedEx.getClass(),
                                        () -> controller.create(HAPPY_SERVICE_INSTANCE_ID,
                                                                HAPPY_ACCEPTS_INCOMPLETE,
                                                                request,
                                                                HAPPY_ORIGINATING_ID,
                                                                HAPPY_REQUEST_ID));
            assertSame(expectedEx, ex);
        }
    }

    @Nested
    class serviceInstanceOperationResponse {

        @BeforeEach
        void setUp() throws ServiceDefinitionDoesNotExistException, ServiceBrokerException, ServiceInstanceExistsException, ServiceDefinitionPlanDoesNotExistException {
            setupMocksForSuccessfulMaintenanceInfoCheck();
            when(deploymentService.createServiceInstance(HAPPY_SERVICE_INSTANCE_ID, request))
                    .thenReturn(operationResponse);
        }

        void validateResponse(ResponseEntity<ServiceInstanceOperationResponse> response, boolean async) {
            assertEquals((async ? HttpStatus.ACCEPTED : HttpStatus.CREATED),
                         response.getStatusCode());
            assertSame(operationResponse, response.getBody());
        }

        @Test
        void asyncResponse() throws AsyncRequiredException, ServiceDefinitionPlanDoesNotExistException, ServiceBrokerException, MaintenanceInfoVersionsDontMatchException, ServiceDefinitionDoesNotExistException, ServiceInstanceExistsException {
            when(operationResponse.isAsync())
                    .thenReturn(true);
            ResponseEntity<ServiceInstanceOperationResponse> response = controller.create(HAPPY_SERVICE_INSTANCE_ID,
                                                                                          HAPPY_ACCEPTS_INCOMPLETE,
                                                                                          request,
                                                                                          HAPPY_ORIGINATING_ID,
                                                                                          HAPPY_REQUEST_ID);
            validateResponse(response, true);
        }

        @Test
        void syncResponse() throws AsyncRequiredException, ServiceDefinitionPlanDoesNotExistException, ServiceBrokerException, MaintenanceInfoVersionsDontMatchException, ServiceDefinitionDoesNotExistException, ServiceInstanceExistsException {
            when(operationResponse.isAsync())
                    .thenReturn(false);
            ResponseEntity<ServiceInstanceOperationResponse> response = controller.create(HAPPY_SERVICE_INSTANCE_ID,
                                                                                          HAPPY_ACCEPTS_INCOMPLETE,
                                                                                          request,
                                                                                          HAPPY_ORIGINATING_ID,
                                                                                          HAPPY_REQUEST_ID);
            validateResponse(response, false);
        }

    }

}
