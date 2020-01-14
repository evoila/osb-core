package de.evoila.cf.broker.controller.core.ServiceInstanceControllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.evoila.cf.broker.exception.AsyncRequiredException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstanceOperationResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class DeleteTest extends BaseTest {

    @Test
    void acceptsIncompleteFalse() {
        assertThrows(AsyncRequiredException.class,
                     () -> controller.delete(HAPPY_ORIGINATING_ID,
                                             HAPPY_REQUEST_ID,
                                             HAPPY_SERVICE_INSTANCE_ID,
                                             false,
                                             HAPPY_SERVICE_ID,
                                             HAPPY_PLAN_ID));
    }

    @Test
    void acceptsIncompleteNull() {
        assertThrows(AsyncRequiredException.class,
                     () -> controller.delete(HAPPY_ORIGINATING_ID,
                                             HAPPY_REQUEST_ID,
                                             HAPPY_SERVICE_INSTANCE_ID,
                                             null,
                                             HAPPY_SERVICE_ID,
                                             HAPPY_PLAN_ID));
    }

    @Test
    void deleteServiceInstanceThrows() throws ServiceInstanceDoesNotExistException, ServiceBrokerException, ServiceDefinitionDoesNotExistException {
        Exception[] exceptions = {
                new ServiceBrokerException(),
                new ServiceDefinitionDoesNotExistException(HAPPY_SERVICE_DEFINITION_ID),
                new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID)
        };
        when(deploymentService.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenThrow(exceptions);
        for (Exception expectedEx : exceptions) {
            Exception ex = assertThrows(expectedEx.getClass(),
                                        () -> controller.delete(HAPPY_ORIGINATING_ID,
                                                                HAPPY_REQUEST_ID,
                                                                HAPPY_SERVICE_INSTANCE_ID,
                                                                HAPPY_ACCEPTS_INCOMPLETE,
                                                                HAPPY_SERVICE_ID,
                                                                HAPPY_PLAN_ID));
            assertSame(expectedEx, ex);
        }
    }

    @Test
    void serviceInstanceOperationResponse() throws ServiceInstanceDoesNotExistException, ServiceBrokerException, ServiceDefinitionDoesNotExistException, AsyncRequiredException {
        when(deploymentService.deleteServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenReturn(operationResponse);
        ResponseEntity<ServiceInstanceOperationResponse> response = controller.delete(HAPPY_ORIGINATING_ID,
                                                                                      HAPPY_REQUEST_ID,
                                                                                      HAPPY_SERVICE_INSTANCE_ID,
                                                                                      HAPPY_ACCEPTS_INCOMPLETE,
                                                                                      HAPPY_SERVICE_ID,
                                                                                      HAPPY_PLAN_ID);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertSame(operationResponse, response.getBody());
    }

}
