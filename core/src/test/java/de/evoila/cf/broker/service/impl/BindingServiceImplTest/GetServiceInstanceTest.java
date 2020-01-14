package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class GetServiceInstanceTest extends BaseTest {

    @Test
    void exceptionThrown() throws ServiceInstanceDoesNotExistException {
        ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException("Mock");
        when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenThrow(expectedEx);
        ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                               () -> service.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID));
        assertSame(expectedEx, ex);
    }

    @Test
    void serviceInstanceReturned() throws ServiceInstanceDoesNotExistException {
        when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenReturn(serviceInstance);
        ServiceInstance result = service.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID);
        assertSame(serviceInstance, result);
    }

}
