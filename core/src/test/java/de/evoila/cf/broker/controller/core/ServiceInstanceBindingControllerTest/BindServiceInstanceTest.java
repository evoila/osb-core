package de.evoila.cf.broker.controller.core.ServiceInstanceBindingControllerTest;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    void emptyAppGuid() throws AsyncRequiredException, PlatformException, ServiceInstanceBindingExistsException, ServiceBrokerException, ServiceDefinitionDoesNotExistException, InvalidParametersException, ConcurrencyErrorException, ServiceDefinitionPlanDoesNotExistException {
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

    @Nested
    class createServiceInstanceBindingThrows {

        @Mock
        private ServiceDefinition serviceDefinition;

        @BeforeEach
        void setUp() throws ServiceDefinitionDoesNotExistException, ServiceDefinitionPlanDoesNotExistException {
            when(request.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            when(request.getPlanId()).thenReturn(HAPPY_PLAN_ID);
            when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID)).thenReturn(serviceDefinition);
            when(serviceDefinition.isPlanBindable(HAPPY_PLAN_ID)).thenReturn(true);
        }

        @SuppressWarnings("unchecked")
        @Test
        void caught() throws AsyncRequiredException, PlatformException, ServiceInstanceBindingExistsException, ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, InvalidParametersException, ConcurrencyErrorException, ServiceDefinitionPlanDoesNotExistException {
            ServiceInstanceDoesNotExistException expectedException = new ServiceInstanceDoesNotExistException("Test9");
            when(bindingService.createServiceInstanceBinding(HAPPY_BINDING_ID,
                                                             HAPPY_INSTANCE_ID,
                                                             request,
                                                             HAPPY_ACCEPTS_INCOMPLETE))
                    .thenThrow(expectedException);
            ServiceBrokerErrorResponse expectedErrorResponse = new ServiceBrokerErrorResponse(expectedException.getError(), expectedException.getMessage());
            ResponseEntity<ServiceBrokerErrorResponse> response = controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                                                                                                 HAPPY_BINDING_ID,
                                                                                                 HAPPY_API_HEADER,
                                                                                                 HAPPY_REQUEST_ID,
                                                                                                 HAPPY_ORIGINATING_ID,
                                                                                                 HAPPY_ACCEPTS_INCOMPLETE,
                                                                                                 request);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals(expectedErrorResponse, response.getBody());
        }

        @Test
        void notCaught() throws AsyncRequiredException, PlatformException, ServiceInstanceBindingExistsException, ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, InvalidParametersException, ConcurrencyErrorException, ServiceDefinitionPlanDoesNotExistException {
            Exception[] exceptions = {
                    new ServiceInstanceBindingExistsException("Test1", "Test2"),
                    new ServiceInstanceBindingExistsException("Test3", "Test4", true, new ServiceInstanceBindingResponse()),
                    new ServiceBrokerException(),
                    new ServiceDefinitionDoesNotExistException("Test5"),
                    new InvalidParametersException("Test6"),
                    new AsyncRequiredException(),
                    new ValidationException("Test7"),
                    new PlatformException("Test8")
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

            when(serviceInstanceUtils.isBlocked(HAPPY_INSTANCE_ID, JobProgress.BIND))
                    .thenReturn(true);
            ConcurrencyErrorException expectedEx = new ConcurrencyErrorException("Service Instance");
            ConcurrencyErrorException ex = assertThrows(expectedEx.getClass(), () -> controller.bindServiceInstance(HAPPY_INSTANCE_ID,
                    HAPPY_BINDING_ID,
                    HAPPY_API_HEADER,
                    HAPPY_REQUEST_ID,
                    HAPPY_ORIGINATING_ID,
                    HAPPY_ACCEPTS_INCOMPLETE,
                    request));
            assertEquals(expectedEx.getError(), ex.getError());
            assertEquals(expectedEx.getDescription(), ex.getDescription());
        }

    }

    @SuppressWarnings("unchecked")
    @Nested
    class asyncBindingResponse {

        private ResponseEntity<BaseServiceInstanceBindingResponse> response;

        @BeforeEach
        void setUp() throws AsyncRequiredException, PlatformException, ServiceInstanceBindingExistsException, ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, InvalidParametersException, ServiceDefinitionPlanDoesNotExistException {
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
        void validIdentityHeaders() throws AsyncRequiredException, PlatformException, ServiceInstanceBindingExistsException, ServiceBrokerException, ServiceDefinitionDoesNotExistException, InvalidParametersException, ConcurrencyErrorException, ServiceDefinitionPlanDoesNotExistException {
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
        void acceptsIncompleteTrue() throws AsyncRequiredException, PlatformException, ServiceInstanceBindingExistsException, ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, InvalidParametersException, ConcurrencyErrorException, ServiceDefinitionPlanDoesNotExistException {
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
        void acceptsIncompleteFalse() throws AsyncRequiredException, PlatformException, ServiceInstanceBindingExistsException, ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, InvalidParametersException, ConcurrencyErrorException, ServiceDefinitionPlanDoesNotExistException {
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
    }

}
