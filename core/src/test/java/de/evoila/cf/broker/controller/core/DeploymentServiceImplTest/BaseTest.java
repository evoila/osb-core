package de.evoila.cf.broker.controller.core.DeploymentServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.JobRepository;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.AsyncDeploymentService;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.impl.DeploymentServiceImpl;

@ExtendWith(MockitoExtension.class)
class BaseTest {

    static final String HAPPY_REFERENCE_ID          = "7314d0bb-e142-4413-898c-f305554bb812";
    static final String HAPPY_PROGRESS_STATE        = "TestState";
    static final String HAPPY_PROGRESS_DESCRIPTION  = "TestDescription";
    static final String HAPPY_SERVICE_INSTANCE_ID   = "79fab675-5abc-4708-9431-3c368599d8b1";
    static final String HAPPY_JOB_PROGRESS_ID       = "d907b873-8c4f-4895-b895-e682a93b12fd";

    @Mock
    PlatformRepository platformRepository;
    @Mock
    ServiceDefinitionRepository serviceDefinitionRepository;
    @Mock
    ServiceInstanceRepository serviceInstanceRepository;
    @Mock
    JobRepository jobRepository;
    @Mock
    AsyncDeploymentService asyncDeploymentService;
    @Mock
    CatalogService catalogService;

    @Mock
    ServiceInstance serviceInstance;
    @Mock
    JobProgress jobProgress;

    DeploymentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DeploymentServiceImpl(platformRepository,
                                            serviceDefinitionRepository,
                                            serviceInstanceRepository,
                                            jobRepository,
                                            asyncDeploymentService,
                                            catalogService);
    }

}
