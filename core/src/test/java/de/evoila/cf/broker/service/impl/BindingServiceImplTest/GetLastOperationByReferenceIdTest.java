package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.ServiceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.model.JobProgressResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class GetLastOperationByReferenceIdTest extends BaseTest {

    @Test
    void getProgressByReferenceIdReturnsNull() {
        ServiceInstanceBindingDoesNotExistsException expectedEx = new ServiceInstanceBindingDoesNotExistsException(HAPPY_OPERATION_ID);
        when(asyncBindingService.getProgressByReferenceId(HAPPY_OPERATION_ID))
                .thenReturn(null);
        ServiceInstanceBindingDoesNotExistsException ex = assertThrows(ServiceInstanceBindingDoesNotExistsException.class,
                                                                       () -> service.getLastOperationByReferenceId(HAPPY_OPERATION_ID));
        assertEquals(expectedEx, ex);
    }

    @Nested
    class getProgressByReferenceIdReturnsNotNull {

        @BeforeEach
        void setUp() {
            when(asyncBindingService.getProgressByReferenceId(HAPPY_OPERATION_ID))
                    .thenReturn(jobProgress);
        }

        @Test
        void containsInternalBindingIdReturnsFalse() {
            when(bindingRepository.containsInternalBindingId(HAPPY_OPERATION_ID))
                    .thenReturn(false);
            ServiceInstanceBindingDoesNotExistsException expectedEx = new ServiceInstanceBindingDoesNotExistsException(HAPPY_OPERATION_ID);
            ServiceInstanceBindingDoesNotExistsException ex = assertThrows(ServiceInstanceBindingDoesNotExistsException.class,
                                                                           () -> service.getLastOperationByReferenceId(HAPPY_OPERATION_ID));
            assertEquals(expectedEx, ex);
        }

        @Test
        void containsInternalBindingIdReturnsTrue() throws ServiceInstanceBindingDoesNotExistsException {
            when(bindingRepository.containsInternalBindingId(HAPPY_OPERATION_ID))
                    .thenReturn(true);
            when(jobProgress.getState())
                    .thenReturn("Mock");
            when(jobProgress.getDescription())
                    .thenReturn("MockDescription");
            JobProgressResponse expectedResponse = new JobProgressResponse(jobProgress);
            JobProgressResponse response = service.getLastOperationByReferenceId(HAPPY_OPERATION_ID);
            assertEquals(expectedResponse, response);
        }

    }

}
