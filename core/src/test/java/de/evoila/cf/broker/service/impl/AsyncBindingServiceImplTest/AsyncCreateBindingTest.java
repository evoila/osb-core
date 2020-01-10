package de.evoila.cf.broker.service.impl.AsyncBindingServiceImplTest;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class AsyncCreateBindingTest extends BaseTest {

    @Mock
    ServiceInstanceBindingRequest serviceInstanceBindingRequest;

    private final static String DESCRIPTION = "Creating binding..";

    @Test
    @DisplayName("Should log exception, when jobStart(...) throws ServiceBroker Exception")
    void startJobThrowsServiceBrokerException() throws ServiceBrokerException {
        ServiceBrokerException serviceBrokerException = new ServiceBrokerException("Test");
        mockStartJobThrowsException(JobProgress.BIND, DESCRIPTION, serviceBrokerException);
        runCreateBinding();
    }

    @Test
    @DisplayName("Should log exception, when jobStart(...), throws RuntimeException")
    void startJobThrowsRuntimeException() throws ServiceBrokerException {
        RuntimeException runtimeException = new RuntimeException("Test");
        mockStartJobThrowsException(JobProgress.BIND, DESCRIPTION, runtimeException);
        runCreateBinding();
        verify(jobProgressService, times(1))
                .failJob(JOB_PROGRESS_ID, "Internal error during instance binding creation, please contact our support.");
    }

    @Test
    @DisplayName("Should log exception, when createBinding(...) throws ServiceBrokerException")
    void createBindingThrowsServiceBrokerException() throws ServiceBrokerException, InvalidParametersException, PlatformException {
        mockStartJobReturnsJobProgress(JobProgress.BIND, DESCRIPTION);
        mockCreateBindingThrowsException(new ServiceBrokerException("Test"));
        runCreateBinding();
    }

    @Test
    @DisplayName("Should log exception, when createBinding(...) throws RuntimeException")
    void createBindingThrowsRuntimeException() throws ServiceBrokerException, InvalidParametersException, PlatformException {
        mockStartJobReturnsJobProgress(JobProgress.BIND, DESCRIPTION);
        mockCreateBindingThrowsException(new RuntimeException("Test"));
        runCreateBinding();
        verify(jobProgressService, times(1))
                .failJob(JOB_PROGRESS_ID, "Internal error during instance binding creation, please contact our support.");
    }

    @Test
    @DisplayName("Should not fail, when no exceptions occurs.")
    void createBindingSucceeds() throws ServiceBrokerException, PlatformException, InvalidParametersException {
        mockStartJobReturnsJobProgress(JobProgress.BIND, DESCRIPTION);
        runCreateBinding();
        verify(bindingService, times(1)).createBinding(
                SERVICE_BINDING_ID, serviceInstance, serviceInstanceBindingRequest, plan);
        verify(jobProgressService, times(1)).succeedProgress(JOB_PROGRESS_ID, "Instance Binding successfully created");
    }

    private void mockCreateBindingThrowsException(Exception ex) throws RuntimeException, ServiceBrokerException, InvalidParametersException, PlatformException {
        when(bindingService.createBinding(SERVICE_BINDING_ID, serviceInstance, serviceInstanceBindingRequest, plan))
                .thenThrow(ex);
    }

    private void runCreateBinding() {
        asyncBindingService.asyncCreateServiceInstanceBinding(bindingService,
                SERVICE_BINDING_ID,
                serviceInstance,
                serviceInstanceBindingRequest, plan, true, JOB_PROGRESS_ID);
    }
}
