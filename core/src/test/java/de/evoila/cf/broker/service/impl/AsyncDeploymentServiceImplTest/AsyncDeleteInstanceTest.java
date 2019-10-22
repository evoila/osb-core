package de.evoila.cf.broker.service.impl.AsyncDeploymentServiceImplTest;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AsyncDeleteInstanceTest extends BaseTest
{
    private void whensForSyncDeleteInstanceThrowsException(JobProgress returnOfFailJob) throws ServiceBrokerException
    {
        mockSuccessfulStartJob(JobProgress.DELETE);
        ServiceBrokerException expectedException = new ServiceBrokerException("Test");
        doThrow(expectedException).when(deploymentService).syncDeleteInstance(serviceInstance, plan, platformService);
        when(startedJob.getId())
                .thenReturn(JOB_PROGRESS_ID);
        when(jobProgressService.failJob(eq(JOB_PROGRESS_ID), anyString()))
                .thenReturn(returnOfFailJob);
    }

    @Test
    void startJobReturnsNull()
    {
        when(serviceInstance.getId())
                .thenReturn(SERVICE_INSTANCE_ID);
        when(jobProgressService.startJob(eq(JOB_PROGRESS_ID), eq(SERVICE_INSTANCE_ID), anyString(), eq(JobProgress.DELETE)))
                .thenReturn(null);

        asyncDeploymentService.asyncDeleteInstance(null, serviceInstance, null, null, JOB_PROGRESS_ID);
    }

    @Test
    void syncDeleteInstanceThrowsExceptionFailJobReturnsNull() throws ServiceBrokerException {
        whensForSyncDeleteInstanceThrowsException(null);

        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncDeleteInstanceThrowsExceptionFailJobReturnsObject() throws ServiceBrokerException
    {
        whensForSyncDeleteInstanceThrowsException(completedJob);

        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncDeleteInstanceSucceeds() throws ServiceBrokerException
    {
        mockSuccessfulStartJob(JobProgress.DELETE);
        doNothing().when(deploymentService).syncDeleteInstance(serviceInstance, plan, platformService);

        asyncDeploymentService.asyncDeleteInstance(deploymentService, serviceInstance, plan, platformService, JOB_PROGRESS_ID);
    }
}
