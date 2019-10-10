package de.evoila.cf.broker.controller.core.ServiceInstanceBindingControllerTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.evoila.cf.broker.exception.ServiceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.model.JobProgressResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class LastOperationTest extends BaseTest {

    @Mock
    private JobProgressResponse jobProgressResponse;

    private ResponseEntity<JobProgressResponse> response;

    @Nested
    class jobProgressResponse {

        void validateResponse() {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(jobProgressResponse, response.getBody());
        }

        @Test
        void withOperation() throws ServiceInstanceBindingDoesNotExistsException {
            when(bindingService.getLastOperationById(HAPPY_BINDING_ID, HAPPY_OPERATION))
                    .thenReturn(jobProgressResponse);
            response = controller.lastOperation(HAPPY_INSTANCE_ID,
                                                HAPPY_BINDING_ID,
                                                HAPPY_SERVICE_ID,
                                                HAPPY_PLAN_ID,
                                                HAPPY_REQUEST_ID,
                                                HAPPY_ORIGINATING_ID,
                                                HAPPY_OPERATION);
            validateResponse();
        }

        @Test
        void nullOperation() throws ServiceInstanceBindingDoesNotExistsException {
            when(bindingService.getLastOperationByReferenceId(HAPPY_BINDING_ID))
                    .thenReturn(jobProgressResponse);
            response = controller.lastOperation(HAPPY_INSTANCE_ID,
                                                HAPPY_BINDING_ID,
                                                HAPPY_SERVICE_ID,
                                                HAPPY_PLAN_ID,
                                                HAPPY_REQUEST_ID,
                                                HAPPY_ORIGINATING_ID,
                                                null);
            validateResponse();
        }

    }

    @Nested
    class exceptionThrown {

        @Test
        void getLastOperationById() throws ServiceInstanceBindingDoesNotExistsException {
            ServiceInstanceBindingDoesNotExistsException expectedEx = new ServiceInstanceBindingDoesNotExistsException("Test");
            when(bindingService.getLastOperationById(HAPPY_BINDING_ID, HAPPY_OPERATION))
                    .thenThrow(expectedEx);
            ServiceInstanceBindingDoesNotExistsException ex = assertThrows(ServiceInstanceBindingDoesNotExistsException.class,
                                                                           () -> controller.lastOperation(HAPPY_INSTANCE_ID,
                                                                                                          HAPPY_BINDING_ID,
                                                                                                          HAPPY_SERVICE_ID,
                                                                                                          HAPPY_PLAN_ID,
                                                                                                          HAPPY_REQUEST_ID,
                                                                                                          HAPPY_ORIGINATING_ID,
                                                                                                          HAPPY_OPERATION));
            assertSame(expectedEx, ex);
        }

        @Test
        void getLastOperationByReferenceId() throws ServiceInstanceBindingDoesNotExistsException {
            ServiceInstanceBindingDoesNotExistsException expectedEx = new ServiceInstanceBindingDoesNotExistsException("Test");
            when(bindingService.getLastOperationByReferenceId(HAPPY_BINDING_ID))
                    .thenThrow(expectedEx);
            ServiceInstanceBindingDoesNotExistsException ex = assertThrows(ServiceInstanceBindingDoesNotExistsException.class,
                                                                           () -> controller.lastOperation(HAPPY_INSTANCE_ID,
                                                                                                          HAPPY_BINDING_ID,
                                                                                                          HAPPY_SERVICE_ID,
                                                                                                          HAPPY_PLAN_ID,
                                                                                                          HAPPY_REQUEST_ID,
                                                                                                          HAPPY_ORIGINATING_ID,
                                                                                                          null));
            assertSame(expectedEx, ex);
        }

    }

}
