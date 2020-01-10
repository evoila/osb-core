package de.evoila.cf.broker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.repository.JobRepository;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobProgressServiceTest {

    private static final String HAPPY_ID            = "cd1d5c99-b6df-42fb-a131-0f25e761e227";
    private static final String HAPPY_REFERENCE_ID  = "29de7227-06d4-4c6f-a9de-3b01fb4f0462";
    private static final String HAPPY_DESCRIPTION   = "Description";
    private static final String HAPPY_OPERATION     = "Operation";

    @Mock
    private JobRepository jobRepository;
    @Mock
    private JobProgress jobProgress;

    private JobProgressService service;

    @BeforeEach
    void setUp() {
        service = new JobProgressService(jobRepository);
    }

    @Nested
    class getProgressByIdMethod {

        @Test
        void returnsNull() {
            when(jobRepository.getJobProgressById(HAPPY_ID))
                    .thenReturn(null);
            JobProgress result = service.getProgressById(HAPPY_ID);
            assertNull(result);
        }

        @Test
        void returnsJobProgress() {
            when(jobRepository.getJobProgressById(HAPPY_ID))
                    .thenReturn(jobProgress);
            JobProgress result = service.getProgressById(HAPPY_ID);
            assertSame(jobProgress, result);
        }

        @Nested
        class exceptionThrown {

            @Test
            void withGetJobProgressByIdThrowing() {
                NoSuchElementException expectedE = new NoSuchElementException();
                when(jobRepository.getJobProgressById(HAPPY_ID))
                        .thenThrow(expectedE);
                NoSuchElementException e = assertThrows(NoSuchElementException.class,
                                                        () -> service.getProgressById(HAPPY_ID));
                assertSame(expectedE, e);
            }

        }

    }

    @Nested
    class getProgressByReferenceIdMethod {

        @Test
        void returnsNull() {
            when(jobRepository.getJobProgressByReferenceId(HAPPY_REFERENCE_ID))
                    .thenReturn(null);
            JobProgress result = service.getProgressByReferenceId(HAPPY_REFERENCE_ID);
            assertNull(result);
        }

        @Test
        void returnsJobProgress() {
            when(jobRepository.getJobProgressByReferenceId(HAPPY_REFERENCE_ID))
                    .thenReturn(jobProgress);
            JobProgress result = service.getProgressByReferenceId(HAPPY_REFERENCE_ID);
            assertSame(jobProgress, result);
        }

        @Nested
        class exceptionThrown {

            @Test
            void withGetJobProgressByIdThrowing() {
                NoSuchElementException expectedE = new NoSuchElementException();
                when(jobRepository.getJobProgressByReferenceId(HAPPY_REFERENCE_ID))
                        .thenThrow(expectedE);
                NoSuchElementException e = assertThrows(NoSuchElementException.class,
                                                        () -> service.getProgressByReferenceId(HAPPY_REFERENCE_ID));
                assertSame(expectedE, e);
            }

        }

    }

    @Nested
    class startJobMethod {

        @Test
        void returnsNull() {
            when(jobRepository.saveJobProgress(HAPPY_ID,
                                               HAPPY_REFERENCE_ID,
                                               JobProgress.IN_PROGRESS,
                                               HAPPY_DESCRIPTION,
                                               HAPPY_OPERATION))
                    .thenReturn(null);
            JobProgress result = service.startJob(HAPPY_ID,
                                                  HAPPY_REFERENCE_ID,
                                                  HAPPY_DESCRIPTION,
                                                  HAPPY_OPERATION);
            assertNull(result);
        }

        @Test
        void returnsJobProgress() {
            when(jobRepository.saveJobProgress(HAPPY_ID,
                                               HAPPY_REFERENCE_ID,
                                               JobProgress.IN_PROGRESS,
                                               HAPPY_DESCRIPTION,
                                               HAPPY_OPERATION))
                    .thenReturn(jobProgress);
            JobProgress result = service.startJob(HAPPY_ID,
                                                  HAPPY_REFERENCE_ID,
                                                  HAPPY_DESCRIPTION,
                                                  HAPPY_OPERATION);
            assertSame(jobProgress, result);
        }

        @Nested
        class exceptionThrown {

            @Test
            void withGetJobProgressByIdThrowing() {
                RuntimeException expectedE = new RuntimeException();
                when(jobRepository.saveJobProgress(HAPPY_ID,
                                                   HAPPY_REFERENCE_ID,
                                                   JobProgress.IN_PROGRESS,
                                                   HAPPY_DESCRIPTION,
                                                   HAPPY_OPERATION))
                        .thenThrow(expectedE);
                RuntimeException e = assertThrows(RuntimeException.class,
                                                  () -> service.startJob(HAPPY_ID,
                                                                         HAPPY_REFERENCE_ID,
                                                                         HAPPY_DESCRIPTION,
                                                                         HAPPY_OPERATION));
                assertSame(expectedE, e);
            }

        }

    }

    @Nested
    class failJobMethod {

        @Test
        void returnsNull() {
            when(jobRepository.updateJobProgress(HAPPY_ID,
                                                 JobProgress.FAILED,
                                                 HAPPY_DESCRIPTION))
                    .thenReturn(null);
            JobProgress result = service.failJob(HAPPY_ID,
                                                 HAPPY_DESCRIPTION);
            assertNull(result);
        }

        @Test
        void returnsJobProgress() {
            when(jobRepository.updateJobProgress(HAPPY_ID,
                                                 JobProgress.FAILED,
                                                 HAPPY_DESCRIPTION))
                    .thenReturn(jobProgress);
            JobProgress result = service.failJob(HAPPY_ID,
                                                 HAPPY_DESCRIPTION);
            assertSame(jobProgress, result);
        }

        @Nested
        class exceptionThrown {

            @Test
            void withGetJobProgressByIdThrowing() {
                RuntimeException expectedE = new RuntimeException();
                when(jobRepository.updateJobProgress(HAPPY_ID,
                                                     JobProgress.FAILED,
                                                     HAPPY_DESCRIPTION))
                        .thenThrow(expectedE);
                RuntimeException e = assertThrows(RuntimeException.class,
                                                  () -> service.failJob(HAPPY_ID,
                                                                        HAPPY_DESCRIPTION));
                assertSame(expectedE, e);
            }

        }

    }

    @Nested
    class succeedProgressMethod {

        @Test
        void returnsNull() {
            when(jobRepository.updateJobProgress(HAPPY_ID,
                                                 JobProgress.SUCCESS,
                                                 HAPPY_DESCRIPTION))
                    .thenReturn(null);
            JobProgress result = service.succeedProgress(HAPPY_ID,
                                                         HAPPY_DESCRIPTION);
            assertNull(result);
        }

        @Test
        void returnsJobProgress() {
            when(jobRepository.updateJobProgress(HAPPY_ID,
                                                 JobProgress.SUCCESS,
                                                 HAPPY_DESCRIPTION))
                    .thenReturn(jobProgress);
            JobProgress result = service.succeedProgress(HAPPY_ID,
                                                         HAPPY_DESCRIPTION);
            assertSame(jobProgress, result);
        }

        @Nested
        class exceptionThrown {

            @Test
            void withGetJobProgressByIdThrowing() {
                RuntimeException expectedE = new RuntimeException();
                when(jobRepository.updateJobProgress(HAPPY_ID,
                                                     JobProgress.SUCCESS,
                                                     HAPPY_DESCRIPTION))
                        .thenThrow(expectedE);
                RuntimeException e = assertThrows(RuntimeException.class,
                                                  () -> service.succeedProgress(HAPPY_ID,
                                                                                HAPPY_DESCRIPTION));
                assertSame(expectedE, e);
            }

        }
    }

}
