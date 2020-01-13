package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.ServiceInstanceBindingNotFoundException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstanceBinding;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class FetchServiceInstanceBindingTest extends BaseTest {

    @Test
    void findOneThrows() {
        when(bindingRepository.findOne(HAPPY_BINDING_ID))
                .thenThrow(new RuntimeException());
        assertThrows(ServiceInstanceBindingNotFoundException.class,
                     () -> service.fetchServiceInstanceBinding(HAPPY_BINDING_ID,
                                                               HAPPY_SERVICE_INSTANCE_ID));
    }

    @Nested
    class findOneDoesNotThrow {

        @BeforeEach
        void setUp() {
            when(bindingRepository.findOne(HAPPY_BINDING_ID))
                    .thenReturn(serviceInstanceBinding);
        }

        void testForServiceInstanceBindingResult() throws ServiceInstanceBindingNotFoundException {
            ServiceInstanceBinding result = service.fetchServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                HAPPY_SERVICE_INSTANCE_ID);
            assertSame(serviceInstanceBinding, result);
        }

        @Test
        void containsJobProgressReturnsFalse() throws ServiceInstanceBindingNotFoundException {
            when(jobRepository.containsJobProgress(HAPPY_BINDING_ID))
                    .thenReturn(false);
            testForServiceInstanceBindingResult();
        }

        @Nested
        class containsJobProgressReturnsTrue {

            @BeforeEach
            void setUp() {
                when(jobRepository.containsJobProgress(HAPPY_BINDING_ID))
                        .thenReturn(true);
                when(jobRepository.getJobProgressByReferenceId(HAPPY_BINDING_ID))
                        .thenReturn(jobProgress);
            }

            @Nested
            class operationIsBind {

                @BeforeEach
                void setUp() {
                    when(jobProgress.getOperation())
                            .thenReturn(JobProgress.BIND);
                }

                @Test
                void stateInProgress() {
                    when(jobProgress.getState())
                            .thenReturn(JobProgress.IN_PROGRESS);
                    assertThrows(ServiceInstanceBindingNotFoundException.class,
                                 () -> service.fetchServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                           HAPPY_SERVICE_INSTANCE_ID));
                }

                @Test
                void stateNotInProgress() throws ServiceInstanceBindingNotFoundException {
                    String[] states = {
                            JobProgress.UNKNOWN,
                            JobProgress.SUCCESS,
                            JobProgress.FAILED
                    };
                    for (String state : states) {
                        when(jobProgress.getState())
                                .thenReturn(state);
                        testForServiceInstanceBindingResult();
                    }
                }

            }

            @Test
            void operationIsNotBind() throws ServiceInstanceBindingNotFoundException {
                String[] operations = {
                        JobProgress.UNKNOWN,
                        JobProgress.PROVISION,
                        JobProgress.UPDATE,
                        JobProgress.DELETE,
                        JobProgress.UNBIND
                };
                for (String operation : operations) {
                    when(jobProgress.getOperation())
                            .thenReturn(operation);
                    testForServiceInstanceBindingResult();
                }
            }

        }

    }

}
