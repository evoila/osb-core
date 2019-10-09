package de.evoila.cf.broker.controller.core.ServiceInstanceBindingControllerTest;

import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.evoila.cf.broker.exception.AsyncRequiredException;
import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingBadRequestException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.BaseServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.util.EmptyRestResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class BindServiceInstanceTest extends BaseTest {

    @Mock
    private ServiceInstanceBindingRequest request;
    @Mock
    private BaseServiceInstanceBindingResponse bindingResponse;

    @BeforeEach
    void setUp() {
        super.setUp();

        when(request.getAppGuid()).thenReturn("34423438-7a9a-4600-82a4-8fb829cb770e");
    }

    @Test
    void emptyAppGuid() throws Exception {
        when(request.getAppGuid()).thenReturn("");
        ResponseEntity response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                                 HAPPY_BINDING_ID,
                                                                 HAPPY_API_HEADER,
                                                                 HAPPY_REQUEST_ID,
                                                                 HAPPY_ORIGINATING_ID,
                                                                 HAPPY_ACCEPTS_INCOMPLETE,
                                                                 request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(EmptyRestResponse.BODY, response.getBody());
    }

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

    @Test
    void createServiceInstanceBindingThrows() throws Exception {
        Exception[] exceptions = {
                new ServiceInstanceBindingExistsException("Test1", "Test2"),
                new ServiceBrokerException(),
                new ServiceDefinitionDoesNotExistException("Test3"),
                new ServiceInstanceDoesNotExistException("Test4"),
                new InvalidParametersException("Test5"),
                new AsyncRequiredException(),
                new ValidationException("Test6"),
                new PlatformException("Test7")
        };
        when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                         HAPPY_INSTANCE_ID,
                                                         request,
                                                         HAPPY_ACCEPTS_INCOMPLETE))
                .thenThrow(exceptions);
        for (Exception expectedEx : exceptions) {
            Exception ex = assertThrows(expectedEx.getClass(),
                                        () -> controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                                             HAPPY_BINDING_ID,
                                                                             HAPPY_API_HEADER,
                                                                             HAPPY_REQUEST_ID,
                                                                             HAPPY_ORIGINATING_ID,
                                                                             HAPPY_ACCEPTS_INCOMPLETE,
                                                                             request));
            assertSame(expectedEx, ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Nested
    class asyncBindingResponse {

        private ResponseEntity<BaseServiceInstanceBindingResponse> response;

        @BeforeEach
        void setUp() throws Exception {
            when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_INSTANCE_ID,
                                                             request,
                                                             HAPPY_ACCEPTS_INCOMPLETE))
                    .thenReturn(bindingResponse);
            when(bindingResponse.isAsync()).thenReturn(true);
        }

        void validateResponse() {
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            assertSame(bindingResponse, response.getBody());
        }

        @Test
        void validIdentityHeaders() throws Exception {
            response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                      HAPPY_BINDING_ID,
                                                      HAPPY_API_HEADER,
                                                      HAPPY_REQUEST_ID,
                                                      HAPPY_ORIGINATING_ID,
                                                      HAPPY_ACCEPTS_INCOMPLETE,
                                                      request);
            validateResponse();
        }

    }

    @SuppressWarnings("unchecked")
    @Nested
    class syncBindingResponse {

        private ResponseEntity<BaseServiceInstanceBindingResponse> response;

        void validateResponse() {
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertSame(bindingResponse, response.getBody());
        }

        @Test
        void acceptsIncompleteTrue() throws Exception {
            when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_INSTANCE_ID,
                                                             request,
                                                             HAPPY_ACCEPTS_INCOMPLETE))
                    .thenReturn(bindingResponse);
            when(bindingResponse.isAsync()).thenReturn(false);
            response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                      HAPPY_BINDING_ID,
                                                      HAPPY_API_HEADER,
                                                      HAPPY_REQUEST_ID,
                                                      HAPPY_ORIGINATING_ID,
                                                      HAPPY_ACCEPTS_INCOMPLETE,
                                                      request);
            validateResponse();
        }

        @Test
        void acceptsIncompleteFalse() throws Exception {
            final boolean async = false;
            when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_INSTANCE_ID,
                                                             request,
                                                             async))
                    .thenReturn(bindingResponse);
            when(bindingResponse.isAsync()).thenReturn(async);
            response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                      HAPPY_BINDING_ID,
                                                      HAPPY_API_HEADER,
                                                      HAPPY_REQUEST_ID,
                                                      HAPPY_ORIGINATING_ID,
                                                      async,
                                                      request);
            validateResponse();
        }

        @Test
        void acceptsIncompleteNull() throws Exception {
            final boolean async = false;
            when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_INSTANCE_ID,
                                                             request,
                                                             async))
                    .thenReturn(bindingResponse);
            when(bindingResponse.isAsync()).thenReturn(async);
            response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                      HAPPY_BINDING_ID,
                                                      HAPPY_API_HEADER,
                                                      HAPPY_REQUEST_ID,
                                                      HAPPY_ORIGINATING_ID,
                                                      null,
                                                      request);
            validateResponse();
        }

    }

}
