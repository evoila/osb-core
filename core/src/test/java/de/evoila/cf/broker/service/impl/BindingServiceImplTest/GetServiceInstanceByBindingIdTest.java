package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.ServiceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetServiceInstanceByBindingIdTest extends BaseTest {

    private void testForServiceInstanceBindingDoesNotExistException() {
        ServiceInstanceBindingDoesNotExistsException expectedEx = new ServiceInstanceBindingDoesNotExistsException(HAPPY_BINDING_ID);
        ServiceInstanceBindingDoesNotExistsException ex = assertThrows(ServiceInstanceBindingDoesNotExistsException.class,
                                                                       () -> service.getServiceInstanceByBindingId(HAPPY_BINDING_ID));
        assertEquals(expectedEx, ex);
    }

    @Test
    void containsInternalBindingIdReturnsFalse() {
        when(bindingRepository.containsInternalBindingId(HAPPY_BINDING_ID))
                .thenReturn(false);
        testForServiceInstanceBindingDoesNotExistException();
    }

    @Nested
    class containsInternalBindingIdReturnsTrue {

        @BeforeEach
        void setUp() {
            when(bindingRepository.containsInternalBindingId(HAPPY_BINDING_ID))
                    .thenReturn(true);
        }

        @Test
        void getInternalBindingIdReturnsNull() {
            when(bindingRepository.getInternalBindingId(HAPPY_BINDING_ID))
                    .thenReturn(null);
            testForServiceInstanceBindingDoesNotExistException();
        }

        @Nested
        class getInternalBindingIdReturnsNotNull {

            @BeforeEach
            void setUp() {
                when(bindingRepository.getInternalBindingId(HAPPY_BINDING_ID))
                        .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
            }

            @Test
            void getServiceInstanceThrows() throws ServiceInstanceDoesNotExistException {
                ServiceInstanceDoesNotExistException ex = new ServiceInstanceDoesNotExistException("Mock");
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenThrow(ex);
                testForServiceInstanceBindingDoesNotExistException();
                verify(log, times(1))
                        .error("Service Instance does not exist!", ex);
            }

            @Test
            void getServiceInstanceDoesNotThrow() throws ServiceInstanceDoesNotExistException, ServiceInstanceBindingDoesNotExistsException {
                when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                        .thenReturn(serviceInstance);
                ServiceInstance result = service.getServiceInstanceByBindingId(HAPPY_BINDING_ID);
                assertSame(serviceInstance, result);
            }

        }

    }

}
