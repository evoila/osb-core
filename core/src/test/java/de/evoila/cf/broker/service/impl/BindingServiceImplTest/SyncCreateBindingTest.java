package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SyncCreateBindingTest extends BaseTest {

    @Test
    void createBindingThrows() throws ServiceBrokerException, InvalidParametersException, PlatformException {
        Exception[] exceptions = {
                new ServiceBrokerException(),
                new InvalidParametersException("Mock"),
                new PlatformException("Mock")
        };
        doThrow(exceptions)
                .when(service)
                .createBinding(HAPPY_BINDING_ID,
                               serviceInstance,
                               request,
                               plan);
        for (Exception expectedEx : exceptions) {
            Exception ex = assertThrows(expectedEx.getClass(),
                                        () -> service.syncCreateBinding(HAPPY_BINDING_ID,
                                                                        serviceInstance,
                                                                        request,
                                                                        plan));
            assertSame(expectedEx, ex);
        }
    }

    @Test
    void createBindingDoesNotThrow() throws ServiceBrokerException, InvalidParametersException, PlatformException {
        ServiceInstanceBindingResponse expectedResponse = new ServiceInstanceBindingResponse();
        doReturn(expectedResponse)
                .when(service)
                .createBinding(HAPPY_BINDING_ID,
                               serviceInstance,
                               request,
                               plan);
        when(randomString.nextString())
                .thenReturn(HAPPY_JOB_PROGRESS_ID);
        ServiceInstanceBindingResponse response = service.syncCreateBinding(HAPPY_BINDING_ID,
                                                                            serviceInstance,
                                                                            request,
                                                                            plan);
        verify(jobRepository, times(1))
                .saveJobProgress(HAPPY_JOB_PROGRESS_ID,
                                 HAPPY_BINDING_ID,
                                 JobProgress.SUCCESS,
                                 "Successfully created synchronous binding.",
                                 JobProgress.BIND);
        assertSame(expectedResponse, response);
    }

}
