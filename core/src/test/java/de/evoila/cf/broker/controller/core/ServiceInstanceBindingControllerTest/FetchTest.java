package de.evoila.cf.broker.controller.core.ServiceInstanceBindingControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;

import de.evoila.cf.broker.exception.ServiceInstanceBindingNotFoundException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingNotRetrievableException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
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


    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test
    void serviceBrokerErrorResponse() throws Exception {
        when(bindingService.getServiceInstance(HAPPY_INSTANCE_ID))
                .thenThrow(new ServiceInstanceDoesNotExistException("Test"));
        ResponseEntity<ServiceBrokerErrorResponse> response = controller.fetch(HAPPY_INSTANCE_ID,
                                                                               HAPPY_BINDING_ID,
                                                                               HAPPY_ORIGINATING_ID,
                                                                               HAPPY_REQUEST_ID);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ServiceInstanceNotFound",
                     response.getBody().getError());
        assertEquals("No service instance was found for the given id. This could be caused by a desynchronization between broker and platform.",
                     response.getBody().getDescription());
    }

    @Nested
    class exceptionThrown {

        @Test
        void fetchServiceInstanceBinding() throws Exception {
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
        void serviceInstanceBindingNotRetrievableException() throws Exception {
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
            assertEquals("The Service Binding could not be retrievable. You should not attempt to call this endpoint",
                         ex.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    @Nested
    class serviceInstanceBindingResponse {

        private ServiceInstanceBindingResponse bindingResponse;
        private ResponseEntity<ServiceInstanceBindingResponse>  response;

        @BeforeEach
        void setUp() throws Exception {
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
        void validIdentityHeaders() throws Exception {
            response = controller.fetch(HAPPY_INSTANCE_ID,
                                        HAPPY_BINDING_ID,
                                        HAPPY_ORIGINATING_ID,
                                        HAPPY_REQUEST_ID);
            validateResponse();
        }

    }

}
