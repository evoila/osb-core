package de.evoila.cf.broker.service.impl.AsyncDeploymentServiceImplTest;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.JobProgressService;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.impl.AsyncDeploymentServiceImpl;
import de.evoila.cf.broker.service.impl.DeploymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
abstract class BaseTest {

    static final String JOB_PROGRESS_ID = "da8a0796-39af-459f-94c9-0ca056a69462";
    static final String SERVICE_INSTANCE_ID = "6bf78c87-01a2-4450-8030-7f7a6d95530d";
    @Mock
    JobProgressService jobProgressService;
    @Mock
    ServiceInstance serviceInstance;
    @Mock
    DeploymentServiceImpl deploymentService;
    @Mock
    JobProgress startedJob;
    @Mock
    JobProgress completedJob;
    @Mock
    Plan plan;
    @Mock
    PlatformService platformService;
    Map<String, Object> parameters = Map.of("firstKey", 256,
            "secondKey", "StringValue");
    AsyncDeploymentServiceImpl asyncDeploymentService;

    @BeforeEach
    void setUp() {
        asyncDeploymentService = new AsyncDeploymentServiceImpl(jobProgressService);
    }

    void mockSuccessfulStartJob(String jobProgress) throws ServiceBrokerException {
        when(serviceInstance.getId())
                .thenReturn(SERVICE_INSTANCE_ID);
        when(jobProgressService.startJob(eq(JOB_PROGRESS_ID), eq(SERVICE_INSTANCE_ID), anyString(), eq(jobProgress)))
                .thenReturn(startedJob);
    }

    void mockStartJobThrowsServiceBrokerException(String jobProgress, String description) throws ServiceBrokerException {
        ServiceBrokerException serviceBrokerException = new ServiceBrokerException("Test");
        when(serviceInstance.getId())
                .thenReturn(SERVICE_INSTANCE_ID);
        when(jobProgressService.startJob(JOB_PROGRESS_ID, SERVICE_INSTANCE_ID, description, jobProgress))
                .thenThrow(serviceBrokerException);
    }

    void mockStartJobThrowsRuntimeException(String jobProgress, String description) throws ServiceBrokerException {
        RuntimeException runtimeException = new RuntimeException("Test");
        when(serviceInstance.getId())
                .thenReturn(SERVICE_INSTANCE_ID);
        when(jobProgressService.startJob(JOB_PROGRESS_ID, SERVICE_INSTANCE_ID, description, jobProgress))
                .thenThrow(runtimeException);
    }
}