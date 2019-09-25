package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.ResponseMessage;
import de.evoila.cf.broker.model.ServiceBrokerErrorResponse;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseControllerTest {

    private class TestBaseController extends BaseController {}

    private BaseController baseController;

    @BeforeEach
    void setUp() {
        baseController = new TestBaseController();
    }

    @SuppressWarnings("ConstantConditions")
    @Nested
    class processErrorResponse {
        @Test
        void StatusCode() {
            for (HttpStatus status : HttpStatus.values()) {
                ResponseEntity response = baseController.processErrorResponse(status);
                assertEquals(status, response.getStatusCode());
            }
        }

        @Test
        void Message() {
            for (HttpStatus status : HttpStatus.values()) {
                ResponseEntity<ResponseMessage<String>> response = baseController.processErrorResponse("Testmessage", status);
                assertEquals(status, response.getStatusCode());
                assertEquals("Testmessage", response.getBody().getMessage());
            }
        }

        @Test
        void ErrorDescription() {
            for (HttpStatus status : HttpStatus.values()) {
                ResponseEntity<ServiceBrokerErrorResponse> response = baseController.processErrorResponse("Testerror", "Testdescription", status);
                assertEquals(status, response.getStatusCode());
                assertEquals("Testerror", response.getBody().getError());
                assertEquals("Testdescription", response.getBody().getDescription());
            }
        }

        @Test
        void Empty_StatusCode() {
            for (HttpStatus status : HttpStatus.values()) {
                ResponseEntity<String> response = baseController.processEmptyErrorResponse(status);
                assertEquals(status, response.getStatusCode());
                assertEquals("{}", response.getBody());
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Nested
    class handleException {
        @Test
        void ValidationException() {
            ValidationException ex = Mockito.mock(ValidationException.class);
            when(ex.getMessage()).thenReturn("Testmessage");
            ResponseEntity<ResponseMessage<String>> response = baseController.handleException(ex);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Testmessage", response.getBody().getMessage());
        }

        @Test
        void MaintenanceInfoVersionsDontMatchException() {
            MaintenanceInfoVersionsDontMatchException ex = Mockito.mock(MaintenanceInfoVersionsDontMatchException.class);
            when(ex.getError()).thenReturn("Testerror");
            when(ex.getDescription()).thenReturn("Testdescription");
            ResponseEntity<ServiceBrokerErrorResponse> response = baseController.handleException(ex);
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertEquals("Testerror", response.getBody().getError());
            assertEquals("Testdescription", response.getBody().getDescription());
        }

        @Test
        void ConcurrencyErrorException() {
            ConcurrencyErrorException ex = Mockito.mock(ConcurrencyErrorException.class);
            when(ex.getError()).thenReturn("Testerror");
            when(ex.getDescription()).thenReturn("Testdescription");
            ResponseEntity<ServiceBrokerErrorResponse> response = baseController.handleException(ex);
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertEquals("Testerror", response.getBody().getError());
            assertEquals("Testdescription", response.getBody().getDescription());
        }

        @Test
        void AsyncRequiredException() {
            AsyncRequiredException ex = Mockito.mock(AsyncRequiredException.class);
            when(ex.getError()).thenReturn("Testerror");
            when(ex.getDescription()).thenReturn("Testdescription");
            ResponseEntity<ServiceBrokerErrorResponse> response = baseController.handleException(ex);
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertEquals("Testerror", response.getBody().getError());
            assertEquals("Testdescription", response.getBody().getDescription());
        }

        @Test
        void ServiceBrokerFeatureIsNotSupportedException() {
            ServiceBrokerFeatureIsNotSupportedException ex = Mockito.mock(ServiceBrokerFeatureIsNotSupportedException.class);
            ResponseEntity response = baseController.handleException(ex);
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        }

        @Test
        void ServiceDefinitionDoesNotExistException() {
            ServiceDefinitionDoesNotExistException ex = Mockito.mock(ServiceDefinitionDoesNotExistException.class);
            when(ex.getMessage()).thenReturn("Testmessage");
            ResponseEntity<ResponseMessage<String>> response = baseController.handleException(ex);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Testmessage", response.getBody().getMessage());
        }

        @Test
        void ServiceInstanceExistsException_identical() {
            ServiceInstanceExistsException ex = Mockito.mock(ServiceInstanceExistsException.class);
            when(ex.isIdenticalInstance()).thenReturn(true);
            ResponseEntity response = baseController.handleException(ex);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @SuppressWarnings("unchecked")
        @Test
        void ServiceInstanceExistsException_different() {
            ServiceInstanceExistsException ex = Mockito.mock(ServiceInstanceExistsException.class);
            when(ex.getMessage()).thenReturn("Testmessage");
            when(ex.isIdenticalInstance()).thenReturn(false);
            ResponseEntity<ResponseMessage<String>> response = baseController.handleException(ex);
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Testmessage", response.getBody().getMessage());
        }

        @Test
        void ServiceInstanceDoesNotExistException() {
            ServiceInstanceDoesNotExistException ex = Mockito.mock(ServiceInstanceDoesNotExistException.class);
            ResponseEntity response = baseController.handleException(ex);
            assertEquals(HttpStatus.GONE, response.getStatusCode());
        }

        @Test
        void ServiceInstanceNotFoundException() {
            ServiceInstanceNotFoundException ex = Mockito.mock(ServiceInstanceNotFoundException.class);
            ResponseEntity response = baseController.handleException(ex);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        void ServiceInstanceBindingExistsException() {
            ServiceInstanceBindingExistsException ex = Mockito.mock(ServiceInstanceBindingExistsException.class);
            ResponseEntity response = baseController.handleException(ex);
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        void ServiceInstanceBindingNotFoundException() {
            ServiceInstanceBindingNotFoundException ex = Mockito.mock(ServiceInstanceBindingNotFoundException.class);
            ResponseEntity response = baseController.handleException(ex);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        void ServiceInstanceBindingDoesNotExistsException() {
            ServiceInstanceBindingDoesNotExistsException ex = Mockito.mock(ServiceInstanceBindingDoesNotExistsException.class);
            ResponseEntity response = baseController.handleException(ex);
            assertEquals(HttpStatus.GONE, response.getStatusCode());
        }
    }
}