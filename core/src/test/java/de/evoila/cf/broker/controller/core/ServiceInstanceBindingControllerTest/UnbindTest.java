package de.evoila.cf.broker.controller.core.ServiceInstanceBindingControllerTest;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.JobProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.evoila.cf.broker.model.BaseServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.util.EmptyRestResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UnbindTest extends BaseTest {

    @Mock
    private ServiceInstanceBindingRequest request;
    @Mock
    private BaseServiceInstanceBindingResponse bindingResponse;

    @Nested
    class deleteServiceInstanceBindingThrows {

        @Test
        void caught() throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceBrokerException, ConcurrencyErrorException, ServiceInstanceDoesNotExistException {
            Exception[] exceptions = {
                    new ServiceInstanceBindingDoesNotExistsException("Test1"),
                    new ServiceDefinitionDoesNotExistException("Test2")
            };
            when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_PLAN_ID,
                                                             HAPPY_ACCEPTS_INCOMPLETE))
                    .thenThrow(exceptions);
            for (Exception ignored : exceptions) {
                ResponseEntity response = controller.unbind(HAPPY_INSTANCE_ID,
                                                            HAPPY_BINDING_ID,
                                                            HAPPY_SERVICE_ID,
                                                            HAPPY_PLAN_ID,
                                                            HAPPY_ACCEPTS_INCOMPLETE,
                                                            HAPPY_API_HEADER,
                                                            HAPPY_REQUEST_ID,
                                                            HAPPY_ORIGINATING_ID);
                assertEquals(HttpStatus.GONE, response.getStatusCode());
                assertEquals(EmptyRestResponse.BODY, response.getBody());
            }

            when(serviceBindingUtils.isBlocked(HAPPY_BINDING_ID, JobProgress.UNBIND))
                    .thenThrow(new ServiceInstanceBindingDoesNotExistsException(""));
            ResponseEntity response = controller.unbind(HAPPY_INSTANCE_ID,
                    HAPPY_BINDING_ID,
                    HAPPY_SERVICE_ID,
                    HAPPY_PLAN_ID,
                    HAPPY_ACCEPTS_INCOMPLETE,
                    HAPPY_API_HEADER,
                    HAPPY_REQUEST_ID,
                    HAPPY_ORIGINATING_ID);
            assertEquals(HttpStatus.GONE, response.getStatusCode());
            assertEquals(EmptyRestResponse.BODY, response.getBody());
        }


            @Test
            void asyncRequiredException() throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceBrokerException {
                AsyncRequiredException exception = new AsyncRequiredException();
                when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                        HAPPY_PLAN_ID,
                        HAPPY_ACCEPTS_INCOMPLETE))
                        .thenThrow(exception);
                Exception ex = assertThrows(exception.getClass(),
                        () -> controller.unbind(HAPPY_INSTANCE_ID,
                                HAPPY_BINDING_ID,
                                HAPPY_SERVICE_ID,
                                HAPPY_PLAN_ID,
                                HAPPY_ACCEPTS_INCOMPLETE,
                                HAPPY_API_HEADER,
                                HAPPY_REQUEST_ID,
                                HAPPY_ORIGINATING_ID));
                assertSame(ex, exception);
            }

        @Test
        void notCaught() throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceBrokerException {
                    Exception[] exceptions = {
                            new AsyncRequiredException(),
                            new ServiceBrokerException()
                    };
                    when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                            HAPPY_PLAN_ID,
                            HAPPY_ACCEPTS_INCOMPLETE))
                            .thenThrow(exceptions);
                    for (Exception expectedEx : exceptions) {
                        Exception ex = assertThrows(expectedEx.getClass(),
                                () -> controller.unbind(HAPPY_INSTANCE_ID,
                                        HAPPY_BINDING_ID,
                                        HAPPY_SERVICE_ID,
                                        HAPPY_PLAN_ID,
                                        HAPPY_ACCEPTS_INCOMPLETE,
                                        HAPPY_API_HEADER,
                                        HAPPY_REQUEST_ID,
                                        HAPPY_ORIGINATING_ID));
                        assertSame(expectedEx, ex);
                    }
                }

            @Test
            void serviceInstanceDoesNotExist() throws ServiceInstanceDoesNotExistException {
                ServiceInstanceDoesNotExistException exception = new ServiceInstanceDoesNotExistException(HAPPY_INSTANCE_ID);
                when(serviceInstanceUtils.isBlocked(HAPPY_INSTANCE_ID, JobProgress.UNBIND))
                        .thenThrow(exception);
                Exception ex = assertThrows(exception.getClass(),
                        () -> controller.unbind(HAPPY_INSTANCE_ID,
                                HAPPY_BINDING_ID,
                                HAPPY_SERVICE_ID,
                                HAPPY_PLAN_ID,
                                HAPPY_ACCEPTS_INCOMPLETE,
                                HAPPY_API_HEADER,
                                HAPPY_REQUEST_ID,
                                HAPPY_ORIGINATING_ID));

                assertSame(exception, ex);
            }

            @Nested
            class concurrencyException {

                @Test
                void bindingIsBlocked() throws ServiceInstanceBindingDoesNotExistsException {
                    ConcurrencyErrorException exception = new ConcurrencyErrorException("Service Binding");
                    when(serviceBindingUtils.isBlocked(HAPPY_BINDING_ID, JobProgress.UNBIND))
                            .thenReturn(true);
                    ConcurrencyErrorException ex = assertThrows(exception.getClass(),
                            () -> controller.unbind(HAPPY_INSTANCE_ID,
                                    HAPPY_BINDING_ID,
                                    HAPPY_SERVICE_ID,
                                    HAPPY_PLAN_ID,
                                    HAPPY_ACCEPTS_INCOMPLETE,
                                    HAPPY_API_HEADER,
                                    HAPPY_REQUEST_ID,
                                    HAPPY_ORIGINATING_ID));
                    assertEquals(ex.getDescription(), exception.getDescription());
                    assertEquals(ex.getError(), exception.getError());
                }
            }

            @Test
            void instanceIsBlocked() throws ServiceInstanceDoesNotExistException {
                ConcurrencyErrorException exception = new ConcurrencyErrorException("Service Instance");
                when(serviceInstanceUtils.isBlocked(HAPPY_INSTANCE_ID, JobProgress.UNBIND))
                        .thenReturn(true);
                ConcurrencyErrorException ex = assertThrows(exception.getClass(),
                        () -> controller.unbind(HAPPY_INSTANCE_ID,
                                HAPPY_BINDING_ID,
                                HAPPY_SERVICE_ID,
                                HAPPY_PLAN_ID,
                                HAPPY_ACCEPTS_INCOMPLETE,
                                HAPPY_API_HEADER,
                                HAPPY_REQUEST_ID,
                                HAPPY_ORIGINATING_ID));
                assertEquals(ex.getDescription(), exception.getDescription());
                assertEquals(ex.getError(), exception.getError());
            }
    }

    @SuppressWarnings("unchecked")
    @Nested
    class asyncBindingResponse {

        private ResponseEntity<BaseServiceInstanceBindingResponse> response;

        @BeforeEach
        void setUp() throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceBrokerException {
            when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_PLAN_ID,
                                                             HAPPY_ACCEPTS_INCOMPLETE))
                    .thenReturn(bindingResponse);
            when(bindingResponse.isAsync()).thenReturn(HAPPY_ACCEPTS_INCOMPLETE);
        }

        void validateResponse() {
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            assertSame(bindingResponse, response.getBody());
        }

        @Test
        void validIdentityHeaders() throws AsyncRequiredException, ServiceBrokerException, ConcurrencyErrorException, ServiceInstanceDoesNotExistException {
            response = controller.unbind(HAPPY_INSTANCE_ID,
                                         HAPPY_BINDING_ID,
                                         HAPPY_SERVICE_ID,
                                         HAPPY_PLAN_ID,
                                         HAPPY_ACCEPTS_INCOMPLETE,
                                         HAPPY_API_HEADER,
                                         HAPPY_REQUEST_ID,
                                         HAPPY_ORIGINATING_ID);
            validateResponse();
        }

    }

    @Nested
    class syncBindingResponse {

        private ResponseEntity response;

        void validateResponse() {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(EmptyRestResponse.BODY, response.getBody());
        }

        @Test
        void deleteServiceInstanceBindingReturns_Null() throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceBrokerException, ConcurrencyErrorException, ServiceInstanceDoesNotExistException {
            when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_PLAN_ID,
                                                             HAPPY_ACCEPTS_INCOMPLETE))
                    .thenReturn(null);
            response = controller.unbind(HAPPY_INSTANCE_ID,
                                         HAPPY_BINDING_ID,
                                         HAPPY_SERVICE_ID,
                                         HAPPY_PLAN_ID,
                                         HAPPY_ACCEPTS_INCOMPLETE,
                                         HAPPY_API_HEADER,
                                         HAPPY_REQUEST_ID,
                                         HAPPY_ORIGINATING_ID);
            validateResponse();
        }

        @Test
        void acceptsIncompleteTrue() throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceBrokerException, ConcurrencyErrorException, ServiceInstanceDoesNotExistException {
            when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_PLAN_ID,
                                                             HAPPY_ACCEPTS_INCOMPLETE))
                    .thenReturn(bindingResponse);
            when(bindingResponse.isAsync()).thenReturn(false);
            response = controller.unbind(HAPPY_INSTANCE_ID,
                                         HAPPY_BINDING_ID,
                                         HAPPY_SERVICE_ID,
                                         HAPPY_PLAN_ID,
                                         HAPPY_ACCEPTS_INCOMPLETE,
                                         HAPPY_API_HEADER,
                                         HAPPY_REQUEST_ID,
                                         HAPPY_ORIGINATING_ID);
            validateResponse();
        }

        @Test
        void acceptsIncompleteFalse() throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceBrokerException, ConcurrencyErrorException, ServiceInstanceDoesNotExistException {
            final boolean async = false;
            when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_PLAN_ID,
                                                             async))
                    .thenReturn(bindingResponse);
            when(bindingResponse.isAsync()).thenReturn(async);
            response = controller.unbind(HAPPY_INSTANCE_ID,
                                         HAPPY_BINDING_ID,
                                         HAPPY_SERVICE_ID,
                                         HAPPY_PLAN_ID,
                                         async,
                                         HAPPY_API_HEADER,
                                         HAPPY_REQUEST_ID,
                                         HAPPY_ORIGINATING_ID);
            validateResponse();
        }
    }
}
