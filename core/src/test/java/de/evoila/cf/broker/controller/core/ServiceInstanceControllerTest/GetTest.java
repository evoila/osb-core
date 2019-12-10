package de.evoila.cf.broker.controller.core.ServiceInstanceControllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.exception.ConcurrencyErrorException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceNotFoundException;
import de.evoila.cf.broker.exception.ServiceInstanceNotRetrievableException;
import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.ServiceInstanceResponse;
import de.evoila.cf.broker.model.catalog.Dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class GetTest extends BaseTest {

    @Test
    void fetchServiceInstanceThrows() throws ConcurrencyErrorException, ServiceBrokerException, ServiceInstanceNotFoundException {
        Exception[] exceptions = {
                new UnsupportedOperationException(),
                new ServiceBrokerException(),
                new ConcurrencyErrorException(),
                new ServiceInstanceNotFoundException()
        };
        when(deploymentService.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenThrow(exceptions);
        for (Exception expectedEx : exceptions) {
            Exception ex = assertThrows(expectedEx.getClass(),
                                        () -> controller.get(HAPPY_REQUEST_ID,
                                                             HAPPY_ORIGINATING_ID,
                                                             HAPPY_SERVICE_INSTANCE_ID));
            assertSame(expectedEx, ex);
        }
    }

    @Nested
    class fetchServiceInstanceDoesNotThrow {

        @BeforeEach
        void setUp() throws ConcurrencyErrorException, ServiceBrokerException, ServiceInstanceNotFoundException {
            when(deploymentService.fetchServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
        }

        @Test
        void getServiceDefinitionThrows() throws ServiceDefinitionDoesNotExistException {
            ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID);
            when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                    .thenThrow(expectedEx);
            ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                     () -> controller.get(HAPPY_REQUEST_ID,
                                                                                          HAPPY_ORIGINATING_ID,
                                                                                          HAPPY_SERVICE_INSTANCE_ID));
            assertSame(expectedEx, ex);
        }

        @Nested
        class getServiceDefinitionDoesNotThrow {

            @BeforeEach
            void setUp() throws ServiceDefinitionDoesNotExistException {
                when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                        .thenReturn(serviceDefinition);
            }

            @Test
            void isInstancesRetrievableReturnsFalse() {
                ServiceInstanceNotRetrievableException expectedEx = new ServiceInstanceNotRetrievableException("The Service Instance is not retrievable. You should not attempt to call this endpoint");
                when(serviceDefinition.isInstancesRetrievable())
                        .thenReturn(false);
                ServiceInstanceNotRetrievableException ex = assertThrows(ServiceInstanceNotRetrievableException.class,
                                                                         () -> controller.get(HAPPY_REQUEST_ID,
                                                                                              HAPPY_ORIGINATING_ID,
                                                                                              HAPPY_SERVICE_INSTANCE_ID));
                assertEquals(expectedEx, ex);

            }

            @Nested
            class isInstancesRetrievableReturnsTrue {

                @Mock
                Dashboard dashboard;
                @Mock
                DashboardClient dashboardClient;

                ServiceInstanceResponse expectedResponse;

                @BeforeEach
                void setUp() {
                    when(serviceDefinition.isInstancesRetrievable())
                            .thenReturn(true);
                    when(serviceInstance.getPlanId())
                            .thenReturn(HAPPY_PLAN_ID);
                }

                private void validateResponse(ResponseEntity<ServiceInstanceResponse> response) {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals(expectedResponse, response.getBody());
                }

                @Test
                void hasDashboardReturnsTrue() throws ConcurrencyErrorException, ServiceInstanceNotFoundException, ServiceBrokerException, ServiceDefinitionDoesNotExistException {
                    when(serviceDefinition.getDashboard())
                            .thenReturn(dashboard);
                    when(dashboard.getUrl())
                            .thenReturn(HAPPY_DASHBOARD_URL);
                    when(serviceDefinition.getDashboardClient())
                            .thenReturn(dashboardClient);
                    expectedResponse = new ServiceInstanceResponse(serviceInstance);
                    expectedResponse.setDashboardUrl(DashboardUtils.dashboard(serviceDefinition, HAPPY_SERVICE_INSTANCE_ID));
                    ResponseEntity<ServiceInstanceResponse> response = controller.get(HAPPY_REQUEST_ID,
                                                                                      HAPPY_ORIGINATING_ID,
                                                                                      HAPPY_SERVICE_INSTANCE_ID);
                    validateResponse(response);
                }

                @Test
                void hasDashboardReturnsFalse() throws ConcurrencyErrorException, ServiceInstanceNotFoundException, ServiceBrokerException, ServiceDefinitionDoesNotExistException {
                    when(serviceDefinition.getDashboard())
                            .thenReturn(null);
                    expectedResponse = new ServiceInstanceResponse(serviceInstance);
                    ResponseEntity<ServiceInstanceResponse> response = controller.get(HAPPY_REQUEST_ID,
                                                                                      HAPPY_ORIGINATING_ID,
                                                                                      HAPPY_SERVICE_INSTANCE_ID);
                    validateResponse(response);
                }

            }

        }

    }

}
