package de.evoila.cf.broker.service.impl.AsyncDeploymentServiceImplTest;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AsyncCreateInstanceTest extends BaseTest {

    private static final String JOB_PROGRESS_DESCRIPTION = "Creating service..";


    private void createInstanceThrowsException(Exception expectedException) throws ServiceBrokerException{
        mockSuccessfulStartJob(JobProgress.PROVISION);
        when(deploymentService.syncCreateInstance(serviceInstance, parameters, plan, platformService))
                .thenThrow(expectedException);
    }

    private void whensForSyncCreateInstanceThrowsServiceBrokerException() throws ServiceBrokerException {
        createInstanceThrowsException(new ServiceBrokerException("Test"));
    }

    private void whensForSyncCreateInstanceThrowsRuntimeException() throws ServiceBrokerException {
        createInstanceThrowsException(new RuntimeException("Test"));
    }

    private void whensForSyncCreateInstanceSucceeds(JobProgress returnOfSucceedProgress) throws ServiceBrokerException {
        mockSuccessfulStartJob(JobProgress.PROVISION);
        when(deploymentService.syncCreateInstance(serviceInstance, parameters, plan, platformService))
                .thenReturn(serviceInstance);
        when(startedJob.getId())
                .thenReturn(JOB_PROGRESS_ID);
        when(jobProgressService.succeedProgress(eq(JOB_PROGRESS_ID), anyString()))
                .thenReturn(returnOfSucceedProgress);
    }

    @Test
    @DisplayName("Should log exception and update JobProgress object, when startJob(...) throws runtime exception.")
    void startJobThrowsRuntimeException() throws ServiceBrokerException {
        mockStartJobThrowsRuntimeException(JobProgress.PROVISION, JOB_PROGRESS_DESCRIPTION);
        asyncDeploymentService.asyncCreateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);

    }

    @Test
    @DisplayName("Should log exception, when startJob(...) fails with ServiceBrokerException.")
    void startJobThrowsServiceBrokerException() throws ServiceBrokerException {
        mockStartJobThrowsServiceBrokerException(JobProgress.PROVISION, JOB_PROGRESS_DESCRIPTION);

        asyncDeploymentService.asyncCreateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should log exception, when syncCreateInstance(...) fails with ServiceBrokerException.")
    void syncCreateInstanceThrowsServiceBrokerException() throws ServiceBrokerException {
        whensForSyncCreateInstanceThrowsServiceBrokerException();

        asyncDeploymentService.asyncCreateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should log exception and update JobProgress object, when syncCreateInstance(...) fails with RuntimeException.")
    void syncCreateInstanceThrowsRuntimeException() throws ServiceBrokerException {
        whensForSyncCreateInstanceThrowsRuntimeException();

        asyncDeploymentService.asyncCreateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncCreateInstanceSucceedsReturnsNull() throws ServiceBrokerException {
        whensForSyncCreateInstanceSucceeds(null);

        asyncDeploymentService.asyncCreateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should not fail, when no exceptions occurs.")
    void syncCreateInstanceSucceedsReturnsObject() throws ServiceBrokerException {
        whensForSyncCreateInstanceSucceeds(completedJob);

        asyncDeploymentService.asyncCreateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }
}
