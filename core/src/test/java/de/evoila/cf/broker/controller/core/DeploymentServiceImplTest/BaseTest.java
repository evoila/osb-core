package de.evoila.cf.broker.controller.core.DeploymentServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
