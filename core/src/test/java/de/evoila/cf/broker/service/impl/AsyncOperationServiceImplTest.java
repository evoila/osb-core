package de.evoila.cf.broker.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.service.JobProgressService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncOperationServiceImplTest {

    private static final String HAPPY_REFERENCE_ID  = "8103035f-8f10-4bad-b23c-978a673c5db7";
    private static final String HAPPY_PROGRESS_ID   = "6bc6d737-0ae6-4519-8d95-515ba1f10bb9";

    @Mock
    private JobProgressService progressService;

    @Mock
    private JobProgress jobProgress;

    private AsyncOperationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AsyncOperationServiceImpl(progressService);
    }

    @Nested
    class getProgressByReferenceIdMethod {

        @Test
        void unknownJobProgress() {
            JobProgress expectedResult = new JobProgress(JobProgress.UNKNOWN,
                                                         JobProgress.UNKNOWN,
                                                         JobProgress.UNKNOWN,
                                                         "Error during job progress retrieval");
            when(progressService.getProgressByReferenceId(HAPPY_REFERENCE_ID))
                    .thenThrow(new RuntimeException());
            JobProgress result = service.getProgressByReferenceId(HAPPY_REFERENCE_ID);
            // Set the date of expected and actual result, because otherwise they cannot be the same
            expectedResult.setDate(null);
            result.setDate(null);
            assertEquals(expectedResult, result);
        }

        @Test
        void validJobProgress() {
            when(progressService.getProgressByReferenceId(HAPPY_REFERENCE_ID))
                    .thenReturn(jobProgress);
            JobProgress result = service.getProgressByReferenceId(HAPPY_REFERENCE_ID);
            assertSame(jobProgress, result);
        }

    }

    @Nested
    class getProgressByIdMethod {

        @Test
        void unknownJobProgress() {
            JobProgress expectedResult = new JobProgress(JobProgress.UNKNOWN,
                                                         JobProgress.UNKNOWN,
                                                         JobProgress.UNKNOWN,
                                                         "Error during job progress retrieval");
            when(progressService.getProgressById(HAPPY_PROGRESS_ID))
                    .thenThrow(new RuntimeException());
            JobProgress result = service.getProgressById(HAPPY_PROGRESS_ID);
            // Set the date of expected and actual result, because otherwise they cannot be the same
            expectedResult.setDate(null);
            result.setDate(null);
            assertEquals(expectedResult, result);
        }

        @Test
        void validJobProgress() {
            when(progressService.getProgressById(HAPPY_PROGRESS_ID))
                    .thenReturn(jobProgress);
            JobProgress result = service.getProgressById(HAPPY_PROGRESS_ID);
            assertSame(jobProgress, result);
        }

    }

}
