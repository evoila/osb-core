package de.evoila.cf.broker.service.impl.AsyncBindingServiceImplTest;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.JobProgressService;
import de.evoila.cf.broker.service.impl.AsyncBindingServiceImpl;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BaseTest {

    AsyncBindingServiceImpl asyncBindingService;

    static final String JOB_PROGRESS_ID = "da8a0796-39af-459f-94c9-0ca056a69462";
    static final String SERVICE_BINDING_ID = "4546d002-ff99-4a29-9e18-346a4a8e5e3b";

    @Mock
    JobProgressService jobProgressService;

    @Mock
    BindingServiceImpl bindingService;
    @Mock
    Plan plan;
    @Mock
    ServiceInstance serviceInstance;

    @BeforeEach
    void setUp() {
        asyncBindingService = new AsyncBindingServiceImpl(jobProgressService);
    }

    void mockStartJobReturnsJobProgress(String jobProgress, String description) throws ServiceBrokerException {
        when(jobProgressService.startJob(JOB_PROGRESS_ID, SERVICE_BINDING_ID, description, jobProgress))
                .thenReturn(new JobProgress(JOB_PROGRESS_ID, SERVICE_BINDING_ID, jobProgress, description));
    }

    void mockStartJobThrowsException(String jobProgress, String description, Exception exception) throws ServiceBrokerException {
        when(jobProgressService.startJob(JOB_PROGRESS_ID, SERVICE_BINDING_ID, description, jobProgress))
                .thenThrow(exception);
    }
}
