package de.evoila.cf.broker.service.impl.AsyncBindingServiceImplTest;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AsyncDeleteBindingTest extends BaseTest {

    private final static String DESCRIPTION = "Deleting binding..";

    @Test
    @DisplayName("Should log exception, when jobStart(...) throws ServiceBroker Exception")
    void startJobThrowsServiceBrokerException() throws ServiceBrokerException {
        ServiceBrokerException serviceBrokerException = new ServiceBrokerException("Test");
        mockStartJobThrowsException(JobProgress.UNBIND, DESCRIPTION, serviceBrokerException);
        runDeleteBinding();
    }

    @Test
    @DisplayName("Should log exception, when jobStart(...), throws RuntimeException")
    void startJobThrowsRuntimeException() throws ServiceBrokerException {
        RuntimeException runtimeException = new RuntimeException("Test");
        mockStartJobThrowsException(JobProgress.UNBIND, DESCRIPTION, runtimeException);
        runDeleteBinding();
        verify(jobProgressService, times(1))
                .failJob(JOB_PROGRESS_ID, "Internal error during instance binding deletion, please contact our support.");
    }

    @Test
    @DisplayName("Should not fail, when no exceptions occurs.")
    void syncDeleteBindingSucceedsReturnsObject() throws ServiceBrokerException {
        mockStartJobReturnsJobProgress(JobProgress.UNBIND, DESCRIPTION);
        runDeleteBinding();
        verify(bindingService, times(1)).deleteServiceInstanceBinding(
                SERVICE_BINDING_ID, serviceInstance, plan);
    }

    private void runDeleteBinding() {
        asyncBindingService.asyncDeleteServiceInstanceBinding(bindingService,
                SERVICE_BINDING_ID,
                serviceInstance,
                plan, JOB_PROGRESS_ID);
    }

}
