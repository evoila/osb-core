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
