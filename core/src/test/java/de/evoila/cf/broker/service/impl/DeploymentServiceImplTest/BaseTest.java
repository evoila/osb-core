package de.evoila.cf.broker.service.impl.DeploymentServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.JobRepository;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.AsyncDeploymentService;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.impl.DeploymentServiceImpl;
import de.evoila.cf.security.utils.RandomString;

@ExtendWith(MockitoExtension.class)
class BaseTest {

    static final String     HAPPY_REFERENCE_ID          = "7314d0bb-e142-4413-898c-f305554bb812";
    static final String     HAPPY_PROGRESS_STATE        = "TestState";
    static final String     HAPPY_PROGRESS_DESCRIPTION  = "TestDescription";
    static final String     HAPPY_SERVICE_INSTANCE_ID   = "79fab675-5abc-4708-9431-3c368599d8b1";
    static final String     HAPPY_JOB_PROGRESS_ID       = "d907b873-8c4f-4895-b895-e682a93b12fd";
    static final String     HAPPY_SERVICE_DEFINITION_ID = "04af7599-4510-432f-b07a-6b433ffea45b";
    static final String     HAPPY_DASHBOARD_URL         = "https://www.test.com/dashboard";
    static final String     HAPPY_PLAN_ID               = "3099f5a7-620c-4124-9006-e89d4367e599";
    static final String     HAPPY_ORGANIZATION_GUID     = "13f603e5-570a-4dfe-93c2-7ac6dd114924";
    static final String     HAPPY_SPACE_GUID            = "a4ea7a0f-d6a8-451e-a4a7-829094d46a6e";
    static final Platform   HAPPY_PLATFORM              = Platform.EXISTING_SERVICE;

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
    @Mock
    ServiceDefinition serviceDefinition;
    @Mock
    Plan plan;
    @Mock
    PlatformService platformService;
    @Mock
    RandomString randomString;

    DeploymentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DeploymentServiceImpl(platformRepository,
                                            serviceDefinitionRepository,
                                            serviceInstanceRepository,
                                            jobRepository,
                                            asyncDeploymentService,
                                            catalogService);
        try {
            FieldSetter.setField(service,
                                 service.getClass().getDeclaredField("randomString"),
                                 randomString);
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException("Mocking `randomString` failed.", ex);
        }
    }

}
