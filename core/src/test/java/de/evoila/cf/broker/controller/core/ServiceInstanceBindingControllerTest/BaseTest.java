package de.evoila.cf.broker.controller.core.ServiceInstanceBindingControllerTest;

import de.evoila.cf.broker.util.ServiceBindingUtils;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import de.evoila.cf.broker.controller.core.ServiceInstanceBindingController;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;

@ExtendWith(MockitoExtension.class)
abstract class BaseTest {

    static final String     HAPPY_INSTANCE_ID           = "e73d39bd-f720-4729-8746-a5e9f87b18c2";
    static final String     HAPPY_BINDING_ID            = "932240cb-825e-49dc-8593-21cb5dcab7ef";
    static final String     HAPPY_SERVICE_ID            = "48d3ceef-dbf9-43eb-b53e-e3a394873e17";
    static final String     HAPPY_PLAN_ID               = "466f8623-2cc5-4f24-9823-e2533272e190";
    static final String     HAPPY_API_HEADER            = "2.15";
    static final String     HAPPY_REQUEST_ID            = "17e0e6a9-aea6-432c-92dd-280b5bf62dea";
    static final String     HAPPY_ORIGINATING_ID        = "cloudfoundry eyANCiAgInVzZXJfaWQiOiAiNjgzZWE3NDgtMzA5Mi00ZmY0LWI2NTYtMzljYWNjNGQ1MzYwIg0KfQ==";
    static final Boolean    HAPPY_ACCEPTS_INCOMPLETE    = true;
    static final String     HAPPY_OPERATION             = "cbfb4b3f-0653-4877-88ff-51e7dd4d9d23";
    static final String     HAPPY_SERVICE_DEFINITION_ID = "f9dba371-60e3-4c60-8263-bddf29aa400d";

    static final Path resourcePath = Path.of(".",
                                                     "src",
                                                     "test",
                                                     "resources",
                                                     "ServiceInstanceBindingController");

    static final String FILE_EXPECTED_SERVICE_INSTANCE_BINDING = "expectedServiceInstanceBinding.json";

    @Mock
    BindingServiceImpl bindingService;
    @Mock
    CatalogService catalogService;
    @Mock
    ServiceInstanceUtils serviceInstanceUtils;
    @Mock
    ServiceBindingUtils serviceBindingUtils;

    ServiceInstanceBindingController controller;

    @BeforeEach
    void setUp() {
        controller = new ServiceInstanceBindingController(bindingService, catalogService, serviceInstanceUtils, serviceBindingUtils);
    }

}
