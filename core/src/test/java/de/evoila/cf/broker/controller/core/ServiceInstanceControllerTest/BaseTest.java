package de.evoila.cf.broker.controller.core.ServiceInstanceControllerTest;

import de.evoila.cf.broker.controller.utils.JobProgressUtils;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.controller.core.ServiceInstanceController;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceOperationResponse;
import de.evoila.cf.broker.model.catalog.MaintenanceInfo;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.DeploymentService;

@ExtendWith(MockitoExtension.class)
class BaseTest {

    static final String HAPPY_SERVICE_INSTANCE_ID = "00024e5e-9283-4c2e-ab5f-8f3ca22f167e";
    static final boolean HAPPY_ACCEPTS_INCOMPLETE = true;
    static final String HAPPY_REQUEST_ID = "17e0e6a9-aea6-432c-92dd-280b5bf62dea";
    static final String HAPPY_ORIGINATING_ID = "cloudfoundry eyANCiAgInVzZXJfaWQiOiAiNjgzZWE3NDgtMzA5Mi00ZmY0LWI2NTYtMzljYWNjNGQ1MzYwIg0KfQ==";
    static final String HAPPY_SERVICE_DEFINITION_ID = "2a7f2bf9-eae1-4e56-b18d-dc619f4973f9";
    static final String HAPPY_PLAN_ID = "52f8a15b-f475-4c7a-9501-2582cb03569e";
    static final String HAPPY_SERVICE_ID = "59b75e6b-3754-4d39-a931-20c24bcd4380";
    static final String HAPPY_MAINTENANCE_INFO_VERSION = "1.0";
    static final String HAPPY_OPERATION = "05256a93-d363-4371-894b-9106416c8ff1";
    static final String HAPPY_DASHBOARD_URL = "https://www.test.com/dashboard";

    @Mock
    DeploymentService deploymentService;
    @Mock
    EndpointConfiguration endpointConfiguration;
    @Mock
    CatalogService catalogService;
    @Mock
    ServiceInstanceRepository serviceInstanceRepository;

    @Mock
    ServiceInstance serviceInstance;
    @Mock
    ServiceDefinition serviceDefinition;
    @Mock
    Plan plan;
    @Mock
    MaintenanceInfo requestMaintenanceInfo;
    @Mock
    MaintenanceInfo planMaintenanceInfo;
    @Mock
    ServiceInstanceOperationResponse operationResponse;

    @Mock
    ServiceInstanceUtils serviceInstanceUtils;

    @Mock
    JobProgressUtils jobProgressUtils;

    ServiceInstanceController controller;

    @BeforeEach
    void setUp() {
        controller = new ServiceInstanceController(
                deploymentService,
                endpointConfiguration,
                catalogService,
                serviceInstanceRepository,
                serviceInstanceUtils,
                jobProgressUtils
        );
    }

}
