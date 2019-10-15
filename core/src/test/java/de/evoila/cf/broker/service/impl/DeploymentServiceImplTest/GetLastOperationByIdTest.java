package de.evoila.cf.broker.service.impl.DeploymentServiceImplTest;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.JobProgressResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class GetLastOperationByIdTest extends BaseTest {

    @Nested
    class serviceInstanceDoesNotExistExceptionThrown {

        private ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID);

        @Test
        void getProgressByIdReturnsNull() {
            when(asyncDeploymentService.getProgressById(HAPPY_JOB_PROGRESS_ID))
                    .thenReturn(null);
            ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                   () -> service.getLastOperationById(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                      HAPPY_JOB_PROGRESS_ID));
            assertEquals(expectedEx, ex);
        }

        @Test
        void containsServiceInstanceIdReturnsFalse() {
            when(asyncDeploymentService.getProgressById(HAPPY_JOB_PROGRESS_ID))
                    .thenReturn(jobProgress);
            when(serviceInstanceRepository.containsServiceInstanceId(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(false);
            ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                   () -> service.getLastOperationById(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                      HAPPY_JOB_PROGRESS_ID));
            assertEquals(expectedEx, ex);
        }

    }

    @Test
    void jobProgressResponse() throws ServiceInstanceDoesNotExistException {
        when(asyncDeploymentService.getProgressById(HAPPY_JOB_PROGRESS_ID))
                .thenReturn(jobProgress);
        when(serviceInstanceRepository.containsServiceInstanceId(HAPPY_SERVICE_INSTANCE_ID))
                .thenReturn(true);
        when(jobProgress.getState())
                .thenReturn(HAPPY_PROGRESS_STATE);
        when(jobProgress.getDescription())
                .thenReturn(HAPPY_PROGRESS_DESCRIPTION);
        JobProgressResponse expectedResponse = new JobProgressResponse(jobProgress);
        JobProgressResponse response = service.getLastOperationById(HAPPY_SERVICE_INSTANCE_ID,
                                                                    HAPPY_JOB_PROGRESS_ID);
        assertEquals(expectedResponse, response);
    }

}
