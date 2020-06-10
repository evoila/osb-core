package de.evoila.cf.broker.controller.custom;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BindingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomServiceKeysControllerTest {

    private static final String HAPPY_SERVICE_INSTANCE_ID = "2b2312d2-c964-4e3d-9d0c-f8e6543f76fb";
    private static final String HAPPY_BINDING_ID = "2357f8a8-3c5c-4f06-8d90-f8493d8177b2";
    private static final String HAPPY_SERVICE_DEFINITION_ID = "804f5b3a-99a2-4d09-9f9a-28bc0e36dac1";
    private static final String HAPPY_PLAN_ID = "bf5fdc89-bde5-418e-8919-62dba1179ccb";


    @Mock
    private BindingRepository bindingRepository;
    @Mock
    private BindingService bindingService;
    @Mock
    private ServiceInstanceRepository serviceInstanceRepository;

    @Mock
    private ServiceInstanceBinding serviceInstanceBinding;
    @Mock
    private ServiceInstance serviceInstance;

    private CustomServiceKeysController controller;

    @BeforeEach
    void setUp() {
        controller = new CustomServiceKeysController(bindingRepository, bindingService, serviceInstanceRepository);
    }

    @Nested
    class getGeneralInformationMethod {

        private void testAndValidateForBindings(List<ServiceInstanceBinding> bindingList) {
            when(bindingRepository.getBindingsForServiceInstance(HAPPY_SERVICE_INSTANCE_ID)).thenReturn(bindingList);
            Page<ServiceInstanceBinding> expectedPage = new PageImpl<>(bindingList);
            ResponseEntity<Page<ServiceInstanceBinding>> response = controller.getGeneralInformation(HAPPY_SERVICE_INSTANCE_ID);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedPage, response.getBody());
        }

        @Test
        void withEmptyBindingsList() {
            testAndValidateForBindings(new ArrayList<>());
        }

        @Test
        void withNonEmptyBindingsList() {
            testAndValidateForBindings(List.of(new ServiceInstanceBinding("ID1", "INSTANCE1",
                    Map.of("KEY1", "VALUE1", "KEY2", "VALUE2"),
                    "URL1"), new ServiceInstanceBinding("ID2",
                    "INSTANCE2",
                    Map.of("KEY3", "VALUE3", "KEY4", "VALUE4"),
                    "URL2")));
        }
    }

    @Nested
    class getServiceKeyMethod {

        @Nested
        class okResponse {

            private void validateResponse(ResponseEntity<ServiceInstanceBinding> response) {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertSame(serviceInstanceBinding, response.getBody());
            }

            @Test
            void withNullServiceInstanceId() {
                when(bindingRepository.findOne(HAPPY_BINDING_ID))
                        .thenReturn(serviceInstanceBinding);
                ResponseEntity<ServiceInstanceBinding> response = controller.getServiceKey(null,
                        HAPPY_BINDING_ID);
                validateResponse(response);
            }

            @Test
            void withServiceInstanceId() {
                when(bindingRepository.findOne(HAPPY_BINDING_ID))
                        .thenReturn(serviceInstanceBinding);
                ResponseEntity<ServiceInstanceBinding> response = controller.getServiceKey(HAPPY_SERVICE_INSTANCE_ID,
                        HAPPY_BINDING_ID);
                validateResponse(response);
            }

        }

    }

    @Nested
    class createServiceKeyMethod {

        private ServiceInstanceBindingRequest serviceInstanceBindingRequest;

        @BeforeEach
        void setUp() {
            serviceInstanceBindingRequest = new ServiceInstanceBindingRequest(HAPPY_SERVICE_DEFINITION_ID,
                    HAPPY_PLAN_ID);
        }

        @Nested
        class exceptionThrown {

            @Test
            void withGetServiceInstanceThrowing() throws ServiceInstanceDoesNotExistException {
                ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException("Mock");
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenThrow(expectedEx);
                ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                        () -> controller.createServiceKey(HAPPY_SERVICE_INSTANCE_ID));
                assertSame(expectedEx, ex);
            }

            @Test
            void withServiceInstanceNull() throws ServiceInstanceDoesNotExistException {
                ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID);
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID)).thenReturn(null);
                ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                        () -> controller.createServiceKey(HAPPY_SERVICE_INSTANCE_ID));
                assertEquals(expectedEx, ex);
            }

            @Test
            void withCreateServiceInstanceBindingThrowing() throws ServiceInstanceDoesNotExistException, ServiceBrokerFeatureIsNotSupportedException, AsyncRequiredException, ServiceInstanceBindingExistsException, ServiceBrokerException, PlatformException, ServiceDefinitionDoesNotExistException, InvalidParametersException, ServiceDefinitionPlanDoesNotExistException, ServicePlanNotBindableException {
                Exception[] exceptions = {
                        new ServiceInstanceBindingExistsException("Mock", "Mock"),
                        new ServiceBrokerException("Mock"),
                        new ServiceInstanceDoesNotExistException("Mock"),
                        new ServiceDefinitionDoesNotExistException("Mock"),
                        new ServiceBrokerFeatureIsNotSupportedException("Mock", "Mock", "Mock"),
                        new InvalidParametersException("Mock"),
                        new AsyncRequiredException(),
                        new PlatformException("Mock")
                };
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID)).thenReturn(serviceInstance);
                when(serviceInstance.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                when(serviceInstance.getPlanId()).thenReturn(HAPPY_PLAN_ID);
                when(bindingService.createServiceInstanceBinding(anyString(), eq(HAPPY_SERVICE_INSTANCE_ID),
                        eq(serviceInstanceBindingRequest), eq(false))).thenThrow(exceptions);
                for (Exception expectedEx : exceptions) {
                    Exception ex = assertThrows(expectedEx.getClass(),
                            () -> controller.createServiceKey(HAPPY_SERVICE_INSTANCE_ID));
                    assertSame(expectedEx, ex);
                }
            }
        }

        @Test
        void okResponse() throws ServiceInstanceDoesNotExistException, ServiceBrokerFeatureIsNotSupportedException, AsyncRequiredException, ServiceInstanceBindingExistsException, ServiceBrokerException, PlatformException, ServiceDefinitionDoesNotExistException, InvalidParametersException, ServiceDefinitionPlanDoesNotExistException, ServicePlanNotBindableException {
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID)).thenReturn(serviceInstance);
            when(serviceInstance.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            when(serviceInstance.getPlanId()).thenReturn(HAPPY_PLAN_ID);
            when(bindingRepository.findOne(anyString())).thenReturn(serviceInstanceBinding);
            ResponseEntity<ServiceInstanceBinding> response = controller.createServiceKey(HAPPY_SERVICE_INSTANCE_ID);
            ArgumentCaptor<String> uuidCaptor = ArgumentCaptor.forClass(String.class);
            verify(bindingService, times(1))
                    .createServiceInstanceBinding(uuidCaptor.capture(), eq(HAPPY_SERVICE_INSTANCE_ID),
                            eq(serviceInstanceBindingRequest), eq(false));
            verify(bindingRepository, times(1)).findOne(uuidCaptor.getValue());
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(serviceInstanceBinding, response.getBody());
        }
    }

    @Nested
    class deleteMethod {

        @Nested
        class exceptionThrown {

            @Test
            void withGetServiceInstanceThrowing() throws ServiceInstanceDoesNotExistException {
                ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException("Mock");
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenThrow(expectedEx);
                ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                        () -> controller.createServiceKey(HAPPY_SERVICE_INSTANCE_ID));
                assertSame(expectedEx, ex);
            }

            @Test
            void withServiceInstanceNull() throws ServiceInstanceDoesNotExistException {
                ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID);
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(null);
                ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                        () -> controller.createServiceKey(HAPPY_SERVICE_INSTANCE_ID));
                assertEquals(expectedEx, ex);
            }

            @Test
            void withDeleteServiceInstanceBindingThrowing() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, AsyncRequiredException, ServiceBrokerException, ServiceInstanceBindingDoesNotExistsException, ServiceDefinitionPlanDoesNotExistException {
                Exception[] exceptions = {
                        new ServiceBrokerException(),
                        new ServiceInstanceBindingDoesNotExistsException("Mock"),
                        new ServiceDefinitionDoesNotExistException("Mock"),
                        new AsyncRequiredException()
                };
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(serviceInstance);
                when(serviceInstance.getPlanId())
                        .thenReturn(HAPPY_PLAN_ID);
                when(serviceInstance.getServiceDefinitionId())
                        .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
                when(bindingService.deleteServiceInstanceBinding(HAPPY_BINDING_ID, HAPPY_SERVICE_DEFINITION_ID,
                        HAPPY_PLAN_ID,
                        false))
                        .thenThrow(exceptions);
                for (Exception expectedEx : exceptions) {
                    Exception ex = assertThrows(expectedEx.getClass(),
                            () -> controller.delete(HAPPY_SERVICE_INSTANCE_ID, HAPPY_BINDING_ID));
                    assertSame(expectedEx, ex);
                }
            }
        }

        @Test
        void okResponse() throws ServiceInstanceDoesNotExistException, AsyncRequiredException, ServiceInstanceBindingDoesNotExistsException, ServiceBrokerException, ServiceDefinitionDoesNotExistException, ServiceDefinitionPlanDoesNotExistException {
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
            when(serviceInstance.getServiceDefinitionId()).thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            ResponseEntity response = controller.delete(HAPPY_SERVICE_INSTANCE_ID, HAPPY_BINDING_ID);
            verify(bindingService, times(1))
                    .deleteServiceInstanceBinding(HAPPY_BINDING_ID, HAPPY_SERVICE_DEFINITION_ID, HAPPY_PLAN_ID, false);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
        }

    }

}
