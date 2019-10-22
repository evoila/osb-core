package de.evoila.cf.broker.service.impl.AsyncDeploymentServiceImplTest;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AsyncCreateInstanceTest extends BaseTest {

    private void whensForSyncCreateInstanceThrowsException(JobProgress returnOfFailJob) throws ServiceBrokerException
    {
        mockSuccessfulStartJob(JobProgress.PROVISION);
        ServiceBrokerException expectedException = new ServiceBrokerException("Test");
        when(deploymentService.syncCreateInstance(serviceInstance, parameters, plan, platformService))
                .thenThrow(expectedException);
        when(startedJob.getId())
                .thenReturn(JOB_PROGRESS_ID);
        when(jobProgressService.failJob(eq(JOB_PROGRESS_ID), anyString()))
                .thenReturn(returnOfFailJob);
    }

    private void whensForSyncCreateInstanceSucceeds(JobProgress returnOfSucceedProgress) throws ServiceBrokerException
    {
        mockSuccessfulStartJob(JobProgress.PROVISION);
        when(deploymentService.syncCreateInstance(serviceInstance, parameters, plan, platformService))
                .thenReturn(serviceInstance);
        when(startedJob.getId())
                .thenReturn(JOB_PROGRESS_ID);
        when(jobProgressService.succeedProgress(eq(JOB_PROGRESS_ID), anyString()))
                .thenReturn(returnOfSucceedProgress);
    }

    @Test
    void startJobReturnsNull()
    {
        when(serviceInstance.getId())
                .thenReturn(SERVICE_INSTANCE_ID);
        when(jobProgressService.startJob(eq(JOB_PROGRESS_ID), eq(SERVICE_INSTANCE_ID), anyString(), eq(JobProgress.PROVISION)))
                .thenReturn(null);

        asyncDeploymentService.asyncCreateInstance(null, serviceInstance, null, null, null, JOB_PROGRESS_ID);
    }

    @Test
    void syncCreateInstanceThrowsExceptionFailJobReturnsNull() throws ServiceBrokerException {
        whensForSyncCreateInstanceThrowsException(null);

        asyncDeploymentService.asyncCreateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncCreateInstanceThrowsExceptionFailJobReturnsObject() throws ServiceBrokerException
    {
        whensForSyncCreateInstanceThrowsException(completedJob);

        asyncDeploymentService.asyncCreateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncCreateInstanceSucceedsReturnsNull() throws ServiceBrokerException
    {
        whensForSyncCreateInstanceSucceeds(null);

        asyncDeploymentService.asyncCreateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncCreateInstanceSucceedsReturnsObject() throws ServiceBrokerException
    {
        whensForSyncCreateInstanceSucceeds(completedJob);

        asyncDeploymentService.asyncCreateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }
}
