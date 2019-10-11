package de.evoila.cf.broker.controller.core.ServiceInstanceBindingControllerTest;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.JobProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    @Test
    void acceptsIncompleteAndApi2_13() {
        assertThrows(ServiceInstanceBindingBadRequestException.class,
                     () -> controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                          HAPPY_BINDING_ID,
                                                          "2.13",
                                                          HAPPY_REQUEST_ID,
                                                          HAPPY_ORIGINATING_ID,
                                                          HAPPY_ACCEPTS_INCOMPLETE,
                                                          request));
    }


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
        }

        @Test
        void notCaught() throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceInstanceDoesNotExistException {
            Exception[] exceptions = {
                    new AsyncRequiredException()
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

    }

    @SuppressWarnings("unchecked")
    @Nested
    class asyncBindingResponse {

        private ResponseEntity<BaseServiceInstanceBindingResponse> response;

        @BeforeEach
        void setUp() throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException {
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
        void validIdentityHeaders() throws AsyncRequiredException, ServiceBrokerException, ConcurrencyErrorException, ServiceInstanceDoesNotExistException, ServiceInstanceBindingDoesNotExistsException {
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

        @Test
        void acceptsIncompleteNull() throws ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceBrokerException, ConcurrencyErrorException, ServiceInstanceDoesNotExistException {
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
                                         null,
                                         HAPPY_API_HEADER,
                                         HAPPY_REQUEST_ID,
                                         HAPPY_ORIGINATING_ID);
            validateResponse();
        }

    }

}
