package de.evoila.cf.broker.controller.core.DeploymentServiceImplTest;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstanceOperationResponse;
import de.evoila.cf.broker.model.ServiceInstanceUpdateRequest;
import de.evoila.cf.broker.model.context.Context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateServiceInstanceContextTest extends BaseTest {

    @Mock
    private ServiceInstanceUpdateRequest request;
    @Mock
    private Context context;

    @Test
    void getServiceInstanceThrows() throws ServiceInstanceDoesNotExistException {
        ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException(HAPPY_SERVICE_INSTANCE_ID);
        when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenThrow(expectedEx);
        ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                               () -> service.updateServiceInstanceContext(HAPPY_SERVICE_INSTANCE_ID,
                                                                                                          request));
        assertSame(expectedEx, ex);
    }

    @Test
    void getServiceInstanceDoesNotThrow() throws ServiceInstanceDoesNotExistException {
        when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenReturn(serviceInstance);
        when(request.getContext())
                .thenReturn(context);
        ServiceInstanceOperationResponse response = service.updateServiceInstanceContext(HAPPY_SERVICE_INSTANCE_ID,
                                                                                         request);
        verify(serviceInstance, times(1))
                .setContext(context);
        verify(serviceInstanceRepository, times(1))
                .updateServiceInstance(serviceInstance);
        assertEquals(new ServiceInstanceOperationResponse(), response);
    }

}
