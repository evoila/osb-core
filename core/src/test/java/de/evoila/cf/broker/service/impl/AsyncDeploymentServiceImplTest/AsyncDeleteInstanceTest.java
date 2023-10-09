package de.evoila.cf.broker.service.impl.AsyncDeploymentServiceImplTest;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;

class AsyncDeleteInstanceTest extends BaseTest {

    private static final String JOB_PROGRESS_DESCRIPTION = "Deleting service..";

    private void whenSyncDeleteInstanceThrowsException(Exception expectedException) throws ServiceBrokerException {
        mockSuccessfulStartJob(JobProgress.DELETE);
        doThrow(expectedException).when(deploymentService).syncDeleteInstance(serviceInstance, plan, platformService);
    }

    @Test
    @DisplayName("Should log exception, when jobStart(...) throws ServiceBrokerException.")
    void startJobThrowsServiceBrokerException() throws ServiceBrokerException {
        mockStartJobThrowsException(JobProgress.DELETE, new ServiceBrokerException("Test"), JOB_PROGRESS_DESCRIPTION);
        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should log exception and update JobProgress, when jobStart(...) throws RuntimeException.")
    void startJobThrowsRuntimeException() throws ServiceBrokerException {
        mockStartJobThrowsException(JobProgress.DELETE, new ServiceBrokerException("Test"), JOB_PROGRESS_DESCRIPTION);
        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should log exception, when syncDeleteInstance(...) throws ServiceBrokerException.")
    void syncDeleteInstanceThrowsServiceBrokerException() throws ServiceBrokerException {
        whenSyncDeleteInstanceThrowsException(new ServiceBrokerException("Test"));
        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should log exception and update JobProgress, when syncDeleteInstance(...) throws RuntimeException.")
    void syncDeleteInstanceThrowsRuntimeException() throws ServiceBrokerException {
        whenSyncDeleteInstanceThrowsException(new RuntimeException("Test"));
        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should finish properly, when no exception is thrown.")
    void syncDeleteInstanceSucceeds() throws ServiceBrokerException {
        mockSuccessfulStartJob(JobProgress.DELETE);
        doNothing().when(deploymentService).syncDeleteInstance(serviceInstance, plan, platformService);
        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }
}
