package de.evoila.cf.broker.controller.core.ServiceInstanceBindingControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.evoila.cf.broker.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;

import de.evoila.cf.broker.model.ServiceBrokerErrorResponse;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class FetchTest extends BaseTest {

    @Mock
    private ServiceInstance serviceInstance;
    @Mock
    private ServiceDefinition serviceDefinition;


    @SuppressWarnings("unchecked")
    @Test
    void serviceBrokerErrorResponse() throws ServiceInstanceDoesNotExistException, ServiceBrokerException, ServiceDefinitionDoesNotExistException, ServiceInstanceBindingNotFoundException {
        ServiceInstanceDoesNotExistException expectedException = new ServiceInstanceDoesNotExistException("Test");
        ServiceBrokerErrorResponse expectedErrorResponse = new ServiceBrokerErrorResponse(expectedException.getError(), expectedException.getMessage());
        when(bindingService.getServiceInstance(HAPPY_INSTANCE_ID))
                .thenThrow(expectedException);
        ResponseEntity<ServiceBrokerErrorResponse> response = controller.fetch(HAPPY_INSTANCE_ID,
                                                                               HAPPY_BINDING_ID,
                                                                               HAPPY_ORIGINATING_ID,
                                                                               HAPPY_REQUEST_ID);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedErrorResponse, response.getBody());
    }

    @Nested
    class exceptionThrown {

        @Test
        void fetchServiceInstanceBinding() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, ServiceInstanceBindingNotFoundException {
            when(bindingService.getServiceInstance(HAPPY_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                    .thenReturn(serviceDefinition);
            when(serviceDefinition.isBindingsRetrievable())
                    .thenReturn(true);
            ServiceInstanceBindingNotFoundException expectedEx = new ServiceInstanceBindingNotFoundException();
            when(bindingService.fetchServiceInstanceBinding(HAPPY_BINDING_ID, HAPPY_INSTANCE_ID))
                    .thenThrow(expectedEx);
            ServiceInstanceBindingNotFoundException ex = assertThrows(ServiceInstanceBindingNotFoundException.class,
                                                                      () -> controller.fetch(HAPPY_INSTANCE_ID,
                                                                                             HAPPY_BINDING_ID,
                                                                                             HAPPY_ORIGINATING_ID,
                                                                                             HAPPY_REQUEST_ID));
            assertSame(expectedEx, ex);

        }

        @Test
        void serviceInstanceBindingNotRetrievableException() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
            when(bindingService.getServiceInstance(HAPPY_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                    .thenReturn(serviceDefinition);
            when(serviceDefinition.isBindingsRetrievable())
                    .thenReturn(false);
            ServiceInstanceBindingNotRetrievableException ex = assertThrows(ServiceInstanceBindingNotRetrievableException.class,
                                                                            () -> controller.fetch(HAPPY_INSTANCE_ID,
                                                                                                   HAPPY_BINDING_ID,
                                                                                                   HAPPY_ORIGINATING_ID,
                                                                                                   HAPPY_REQUEST_ID));
            assertEquals("The Service Binding is not retrievable. You should not attempt to call this endpoint",
                         ex.getMessage());
        }

        @Test
        void serviceInstanceBindingNotFoundException() throws ServiceInstanceBindingNotFoundException, ServiceDefinitionDoesNotExistException, ServiceInstanceDoesNotExistException {
            when(bindingService.getServiceInstance(HAPPY_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                    .thenReturn(serviceDefinition);
            when(serviceDefinition.isBindingsRetrievable())
                    .thenReturn(true);
            when(bindingService.fetchServiceInstanceBinding(HAPPY_BINDING_ID, HAPPY_INSTANCE_ID))
                    .thenThrow(new ServiceInstanceBindingNotFoundException());

            ServiceInstanceBindingNotFoundException expectedEx = new ServiceInstanceBindingNotFoundException();
            Exception ex = assertThrows(expectedEx.getClass(),
                    () -> controller.fetch(HAPPY_INSTANCE_ID,
                            HAPPY_BINDING_ID,
                            HAPPY_ORIGINATING_ID,
                            HAPPY_REQUEST_ID));
            assertEquals(expectedEx.getMessage(), ex.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    @Nested
    class serviceInstanceBindingResponse {

        private ServiceInstanceBindingResponse bindingResponse;
        private ResponseEntity<ServiceInstanceBindingResponse>  response;

        @BeforeEach
        void setUp() throws IOException, ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, ServiceInstanceBindingNotFoundException {
            String json = Files.readString(resourcePath.resolve(FILE_EXPECTED_SERVICE_INSTANCE_BINDING));
            ServiceInstanceBinding binding = new ObjectMapper().readValue(json, ServiceInstanceBinding.class);
            bindingResponse = new ServiceInstanceBindingResponse(binding);
            when(bindingService.getServiceInstance(HAPPY_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                    .thenReturn(serviceDefinition);
            when(serviceDefinition.isBindingsRetrievable())
                    .thenReturn(true);
            when(bindingService.fetchServiceInstanceBinding(HAPPY_BINDING_ID, HAPPY_INSTANCE_ID))
                    .thenReturn(binding);
        }

        void validateResponse() {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(bindingResponse, response.getBody());
        }

        @Test
        void validIdentityHeaders() throws ServiceBrokerException, ServiceDefinitionDoesNotExistException, ServiceInstanceBindingNotFoundException {
            response = controller.fetch(HAPPY_INSTANCE_ID,
                                        HAPPY_BINDING_ID,
                                        HAPPY_ORIGINATING_ID,
                                        HAPPY_REQUEST_ID);
            validateResponse();
        }

    }

}
