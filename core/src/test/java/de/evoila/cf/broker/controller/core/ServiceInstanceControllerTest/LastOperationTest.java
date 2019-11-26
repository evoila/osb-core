package de.evoila.cf.broker.controller.core.ServiceInstanceControllerTest;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.JobProgressResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class LastOperationTest extends BaseTest {

    @Mock
    JobProgressResponse jobProgressResponse;

    @Test
    void getLastOperationByIdThrows() throws ServiceInstanceDoesNotExistException {
        ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID);
        when(deploymentService.getLastOperationById(HAPPY_SERVICE_INSTANCE_ID, HAPPY_OPERATION))
                .thenThrow(expectedEx);
        ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                               () -> controller.lastOperation(HAPPY_REQUEST_ID,
                                                                                              HAPPY_SERVICE_INSTANCE_ID,
                                                                                              HAPPY_ORIGINATING_ID,
                                                                                              HAPPY_OPERATION));
        assertSame(expectedEx, ex);
    }

    @Test
    void getLastOperationByReferenceIdThrows() throws ServiceInstanceDoesNotExistException {
        ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID);
        when(deploymentService.getLastOperationByReferenceId(HAPPY_SERVICE_INSTANCE_ID))
                .thenThrow(expectedEx);
        ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                               () -> controller.lastOperation(HAPPY_REQUEST_ID,
                                                                                              HAPPY_SERVICE_INSTANCE_ID,
                                                                                              HAPPY_ORIGINATING_ID,
                                                                                              null));
        assertSame(expectedEx, ex);
    }

    private void validateResponse(ResponseEntity<JobProgressResponse> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(jobProgressResponse, response.getBody());
    }

    @Test
    void jobProgressResponseById() throws ServiceInstanceDoesNotExistException {
        when(deploymentService.getLastOperationById(HAPPY_SERVICE_INSTANCE_ID, HAPPY_OPERATION))
                .thenReturn(jobProgressResponse);
        ResponseEntity<JobProgressResponse> response = controller.lastOperation(HAPPY_REQUEST_ID,
                                                                                HAPPY_SERVICE_INSTANCE_ID,
                                                                                HAPPY_ORIGINATING_ID,
                                                                                HAPPY_OPERATION);
        validateResponse(response);
    }


    @Test
    void jobProgressResponseByReferenceId() throws ServiceInstanceDoesNotExistException {
        when(deploymentService.getLastOperationByReferenceId(HAPPY_SERVICE_INSTANCE_ID))
                .thenReturn(jobProgressResponse);
        ResponseEntity<JobProgressResponse> response = controller.lastOperation(HAPPY_REQUEST_ID,
                                                                                HAPPY_SERVICE_INSTANCE_ID,
                                                                                HAPPY_ORIGINATING_ID,
                                                                                null);
        validateResponse(response);
    }

}
