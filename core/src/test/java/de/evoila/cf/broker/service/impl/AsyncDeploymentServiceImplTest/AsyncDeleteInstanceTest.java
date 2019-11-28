package de.evoila.cf.broker.service.impl.AsyncDeploymentServiceImplTest;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class AsyncDeleteInstanceTest extends BaseTest {

    private static final String JOB_PROGRESS_DESCRIPTION = "Deleting service..";

    private void whenSyncDeleteInstanceThrowsException(Exception expectedException) throws ServiceBrokerException {
        mockSuccessfulStartJob(JobProgress.DELETE);
        doThrow(expectedException).when(deploymentService).syncDeleteInstance(serviceInstance, plan, platformService);
    }

    private void whenSyncDeleteInstanceThrowsServiceBrokerException() throws ServiceBrokerException {
        whenSyncDeleteInstanceThrowsException(new ServiceBrokerException("Test"));
    }

    private void whenSyncDeleteInstanceThrowsRuntimeException() throws ServiceBrokerException {
        whenSyncDeleteInstanceThrowsException(new RuntimeException("Test"));
    }

    @Test
    @DisplayName("Should log exception, when jobStart(...) throws ServiceBrokerException.")
    void startJobThrowsServiceBrokerException() throws ServiceBrokerException {
        mockStartJobThrowsServiceBrokerException(JobProgress.DELETE, JOB_PROGRESS_DESCRIPTION);
        asyncDeploymentService.asyncDeleteInstance(null, serviceInstance, null, null, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should log exception and update JobProgress, when jobStart(...) throws ServiceBrokerException.")
    void startJobThrowsRuntimeException() throws ServiceBrokerException {
        mockStartJobThrowsRuntimeException(JobProgress.DELETE, JOB_PROGRESS_DESCRIPTION);
        asyncDeploymentService.asyncDeleteInstance(null, serviceInstance, null, null, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should log exception, when syncDeleteInstance(...) throws ServiceBrokerException.")
    void syncDeleteInstanceThrowsServiceBrokerException() throws ServiceBrokerException {
        whenSyncDeleteInstanceThrowsServiceBrokerException();
        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should log exception and update JobProgress, when syncDeleteInstance(...) throws RuntimeException.")
    void syncDeleteInstanceThrowsRuntimeException() throws ServiceBrokerException {
        whenSyncDeleteInstanceThrowsRuntimeException();
        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should finish properly, when exception occurs.")
    void syncDeleteInstanceSucceeds() throws ServiceBrokerException {
        mockSuccessfulStartJob(JobProgress.DELETE);
        doNothing().when(deploymentService).syncDeleteInstance(serviceInstance, plan, platformService);
        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }
}
