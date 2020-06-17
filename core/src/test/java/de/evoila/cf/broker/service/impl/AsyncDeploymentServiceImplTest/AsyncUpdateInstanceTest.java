package de.evoila.cf.broker.service.impl.AsyncDeploymentServiceImplTest;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.model.JobProgress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;

class AsyncUpdateInstanceTest extends BaseTest {

    private final static String JOB_PROGRESS_DESCRIPTION = "Updating service..";

    private void whensForSyncUpdateInstanceThrowsException(Exception expectedException) throws ServiceBrokerException, ServiceDefinitionDoesNotExistException {
        mockSuccessfulStartJob(JobProgress.UPDATE);
        when(deploymentService.syncUpdateInstance(serviceInstance, parameters, plan, platformService))
                .thenThrow(expectedException);
    }

    private void whensForSyncUpdateInstanceSucceeds() throws ServiceBrokerException, ServiceDefinitionDoesNotExistException {
        mockSuccessfulStartJob(JobProgress.UPDATE);
        when(deploymentService.syncUpdateInstance(serviceInstance, parameters, plan, platformService))
                .thenReturn(serviceInstance);
    }

    @Test
    @DisplayName("Should log exception, when startJob(...) throws ServiceBrokerException")
    void startJobThrowsServiceBrokerException() throws ServiceBrokerException {
        super.mockStartJobThrowsException(JobProgress.UPDATE, new ServiceBrokerException("Test"), JOB_PROGRESS_DESCRIPTION);
        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should log exception and update JobProgress object, when startJob(...) throws RuntimeException")
    void startJobThrowsRuntimeException() throws ServiceBrokerException {
        mockStartJobThrowsException(JobProgress.UPDATE, new RuntimeException("Test"), JOB_PROGRESS_DESCRIPTION);
        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }


    @Test
    @DisplayName("Should log exception, when syncUpdateInstance(...) throws ServiceBrokerException")
    void syncUpdateInstanceThrowsServiceBrokerException() throws ServiceBrokerException, ServiceDefinitionDoesNotExistException {
        whensForSyncUpdateInstanceThrowsException(new ServiceBrokerException("Test"));
        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    @DisplayName("Should log exception, when syncUpdateInstance(...) throws RuntimeException")
    void syncUpdateInstanceThrowsRuntimeException() throws ServiceBrokerException, ServiceDefinitionDoesNotExistException {
        whensForSyncUpdateInstanceThrowsException(new RuntimeException("Test"));

        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }


    @Test
    void syncUpdateInstanceThrowsExceptionFailJobReturnsObject() throws ServiceBrokerException, ServiceDefinitionDoesNotExistException {
        whensForSyncUpdateInstanceThrowsException(new ServiceBrokerException("Test"));

        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncUpdateInstanceSucceedsReturnsNull() throws ServiceBrokerException, ServiceDefinitionDoesNotExistException {
        whensForSyncUpdateInstanceSucceeds();

        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }

    @Test
    void syncUpdateInstanceSucceedsReturnsObject() throws ServiceBrokerException, ServiceDefinitionDoesNotExistException {
        whensForSyncUpdateInstanceSucceeds();

        asyncDeploymentService.asyncUpdateInstance(deploymentService, serviceInstance, parameters, plan, platformService, JOB_PROGRESS_ID);
    }
}
