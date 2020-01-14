package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.BindResource;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.RouteBinding;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.JobRepository;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.repository.RouteBindingRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.security.utils.RandomString;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class BaseTest {

    /**
     * Subclass for making protected methods visible to the tests to make testing the public methods easier.
     * We now can easily mock the protected methods through this class instance.
     * We keep everything else like the superclass as we want to test the superclass.
     */
    static class TestBindingServiceImpl extends BindingServiceImpl {

        public TestBindingServiceImpl(BindingRepository bindingRepository, ServiceDefinitionRepository serviceDefinitionRepository, ServiceInstanceRepository serviceInstanceRepository, RouteBindingRepository routeBindingRepository, JobRepository jobRepository, AsyncBindingService asyncBindingService, PlatformRepository platformRepository) {
            super(bindingRepository, serviceDefinitionRepository, serviceInstanceRepository, routeBindingRepository, jobRepository, asyncBindingService, platformRepository);
        }

        @Override
        protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
            return null;
        }

        @Override
        protected void unbindService(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException, PlatformException {

        }

        @Override
        protected Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest, ServiceInstance serviceInstance, Plan plan, ServerAddress serverAddress) throws ServiceBrokerException, InvalidParametersException, PlatformException {
            return null;
        }

        @Override
        protected void validateBindingNotExists(ServiceInstanceBindingRequest serviceInstanceBindingRequest, String bindingId, String instanceId)
                throws ServiceInstanceBindingExistsException, ServiceInstanceDoesNotExistException {
            super.validateBindingNotExists(serviceInstanceBindingRequest,
                                           bindingId,
                                           instanceId);
        }

        @Override
        protected ServiceInstanceBinding createServiceInstanceBinding(String bindingId, String serviceInstanceId,
                                                                      Map<String, Object> credentials, String syslogDrainUrl, String appGuid) {
            return super.createServiceInstanceBinding(bindingId,
                                                      serviceInstanceId,
                                                      credentials,
                                                      syslogDrainUrl,
                                                      appGuid);
        }

        @Override
        protected ServiceInstance getServiceInstanceByBindingId(String bindingId)
                throws ServiceInstanceBindingDoesNotExistsException {
            return super.getServiceInstanceByBindingId(bindingId);
        }

        @Override
        protected ServiceInstanceBinding bindService(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                     ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException, InvalidParametersException, PlatformException {
            return super.bindService(bindingId, serviceInstanceBindingRequest, serviceInstance, plan);
        }

    }

    static final String     HAPPY_BINDING_ID                = "9781bcb0-a6c9-4eaf-ae4f-aebb4addbb0e";
    static final String     HAPPY_SERVICE_INSTANCE_ID       = "a763ef49-d4d3-4d8e-9755-a0914a3518ca";
    static final boolean    HAPPY_ASYNC                     = true;
    static final String     HAPPY_PLAN_ID                   = "dab13374-703e-442a-9e07-e8f41de54f80";
    static final Platform   HAPPY_PLATFORM                  = Platform.EXISTING_SERVICE;
    static final String     HAPPY_OPERATION_ID              = "f838ba81-47e8-4f27-a3a5-e6fc720e48e8";
    static final String     HAPPY_SYSLOG_DRAIN_URL          = "https://www.test.com/syslog";
    static final String     HAPPY_APP_GUID                  = "08fe8866-ded7-459a-95cf-67af6ec922dd";
    static final String     HAPPY_JOB_PROGRESS_ID           = "39a602d2-f34f-4a7f-9fca-cfac933bbccb";
    static final String     HAPPY_ROUTE                     = "Route";
    static final String     HAPPY_BIND_RESOURCE_APP_GUID    = "9e36ce6d-8ee8-49f4-a82f-12996640188b";
    static final Map<String, Object> HAPPY_CREDENTIALS      = new HashMap<>() {{
        put("KEY1", "VALUE1");
        put("KEY2", "VALUE2");
    }};

    @Mock
    Logger log;
    @Mock
    BindingRepository bindingRepository;
    @Mock
    ServiceDefinitionRepository serviceDefinitionRepository;
    @Mock
    ServiceInstanceRepository serviceInstanceRepository;
    @Mock
    RouteBindingRepository routeBindingRepository;
    @Mock
    JobRepository jobRepository;
    @Mock
    AsyncBindingService asyncBindingService;
    @Mock
    PlatformRepository platformRepository;
    @Mock
    RandomString randomString;

    @Mock
    ServiceInstanceBindingRequest request;
    @Mock
    ServiceInstance serviceInstance;
    @Mock
    Plan plan;
    @Mock
    PlatformService platformService;
    @Mock
    ServiceInstanceBinding serviceInstanceBinding;
    @Mock
    JobProgress jobProgress;
    @Mock
    BindResource bindResource;
    @Mock
    RouteBinding routeBinding;

    @InjectMocks
    TestBindingServiceImpl service = mock(TestBindingServiceImpl.class, Mockito.CALLS_REAL_METHODS);

    @BeforeEach
    void setUp() {
        try {
            FieldSetter.setField(service,
                                 service.getClass()         // TestBindingServiceImpl
                                        .getSuperclass()    // BindingServiceImpl
                                        .getDeclaredField("log"), //
                                 log);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Setting logger failed.", e);
        }
    }

}
