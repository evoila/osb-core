package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
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

    }

    static final String     HAPPY_BINDING_ID            = "9781bcb0-a6c9-4eaf-ae4f-aebb4addbb0e";
    static final String     HAPPY_SERVICE_INSTANCE_ID   = "a763ef49-d4d3-4d8e-9755-a0914a3518ca";
    static final boolean    HAPPY_ASYNC                 = true;
    static final String     HAPPY_PLAN_ID               = "dab13374-703e-442a-9e07-e8f41de54f80";
    static final Platform   HAPPY_PLATFORM              = Platform.EXISTING_SERVICE;
    static final String     HAPPY_OPERATION_ID          = "f838ba81-47e8-4f27-a3a5-e6fc720e48e8";

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

    @InjectMocks
    TestBindingServiceImpl service = mock(TestBindingServiceImpl.class, Mockito.CALLS_REAL_METHODS);

}
