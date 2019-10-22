package de.evoila.cf.broker.service.impl.AsyncDeploymentServiceImplTest;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AsyncUpdateInstanceTest extends BaseTest {

    private void whensForsyncUpdateInstanceThrowsException(JobProgress returnOfFailJob) throws ServiceBrokerException
    {
        mockSuccessfulStartJob(JobProgress.UPDATE);
        ServiceBrokerException expectedException = new ServiceBrokerException("Test");
        when(deploymentService.syncUpdateInstance(serviceInstance, parameters, plan, platformService))
                .thenThrow(expectedException);
        when(startedJob.getId())
                .thenReturn(JOB_PROGRESS_ID);
        when(jobProgressService.failJob(eq(JOB_PROGRESS_ID), anyString()))
                .thenReturn(returnOfFailJob);
    }

    private void whensForSyncUpdateInstanceSucceeds(JobProgress returnOfSucceedProgress) throws ServiceBrokerException
    {
        mockSuccessfulStartJob(JobProgress.UPDATE);
        when(deploymentService.syncUpdateInstance(serviceInstance, parameters, plan, platformService))
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
        when(jobProgressService.startJob(eq(JOB_PROGRESS_ID), eq(SERVICE_INSTANCE_ID), anyString(), eq(JobProgress.UPDATE)))
                .thenReturn(null);

        asyncDeploymentService.asyncUpdateInstance(null, serviceInstance, null, null, null, JOB_PROGRESS_ID);
    }

    @Test
    void syncUpdateInstanceThrowsExceptionFailJobReturnsNull() throws ServiceBrokerException {
        whensForsyncUpdateInstanceThrowsException(null);

        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncUpdateInstanceThrowsExceptionFailJobReturnsObject() throws ServiceBrokerException
    {
        whensForsyncUpdateInstanceThrowsException(completedJob);

        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncUpdateInstanceSucceedsReturnsNull() throws ServiceBrokerException
    {
        whensForSyncUpdateInstanceSucceeds(null);

        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncUpdateInstanceSucceedsReturnsObject() throws ServiceBrokerException
    {
        whensForSyncUpdateInstanceSucceeds(completedJob);

        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }
}
