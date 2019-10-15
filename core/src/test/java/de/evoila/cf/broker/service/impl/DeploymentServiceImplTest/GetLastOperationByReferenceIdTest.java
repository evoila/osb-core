package de.evoila.cf.broker.service.impl.DeploymentServiceImplTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.JobProgressResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class GetLastOperationByReferenceIdTest extends BaseTest {

    @Nested
    class serviceInstanceDoesNotExistExceptionThrown {

        private ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException(HAPPY_REFERENCE_ID);

        @Test
        void getProgressByReferenceIdReturnsNull() {
            when(asyncDeploymentService.getProgressByReferenceId(HAPPY_REFERENCE_ID))
                    .thenReturn(null);
            ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                   () -> service.getLastOperationByReferenceId(HAPPY_REFERENCE_ID));
            assertEquals(expectedEx, ex);
        }

        @Test
        void containsServiceInstanceIdReturnsFalse() {
            when(asyncDeploymentService.getProgressByReferenceId(HAPPY_REFERENCE_ID))
                    .thenReturn(jobProgress);
            when(serviceInstanceRepository.containsServiceInstanceId(HAPPY_REFERENCE_ID))
                    .thenReturn(false);
            ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                   () -> service.getLastOperationByReferenceId(HAPPY_REFERENCE_ID));
            assertEquals(expectedEx, ex);
        }

    }

    @Test
    void jobProgressResponse() throws ServiceInstanceDoesNotExistException {
        when(asyncDeploymentService.getProgressByReferenceId(HAPPY_REFERENCE_ID))
                .thenReturn(jobProgress);
        when(serviceInstanceRepository.containsServiceInstanceId(HAPPY_REFERENCE_ID))
                .thenReturn(true);
        when(jobProgress.getState())
                .thenReturn(HAPPY_PROGRESS_STATE);
        when(jobProgress.getDescription())
                .thenReturn(HAPPY_PROGRESS_DESCRIPTION);
        JobProgressResponse expectedResponse = new JobProgressResponse(jobProgress);
        JobProgressResponse response = service.getLastOperationByReferenceId(HAPPY_REFERENCE_ID);
        assertEquals(expectedResponse, response);
    }

}
