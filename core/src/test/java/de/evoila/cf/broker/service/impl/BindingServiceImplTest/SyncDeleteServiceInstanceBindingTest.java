package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SyncDeleteServiceInstanceBindingTest extends BaseTest {

    @Test
    void allMethodsCalled() {
        doNothing()
                .when(service)
                .deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                              serviceInstance,
                                              plan);
        service.syncDeleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                                 serviceInstance,
                                                 plan);
        verify(service, times(1))
                .deleteServiceInstanceBinding(HAPPY_BINDING_ID,
                                              serviceInstance,
                                              plan);
        verify(jobRepository, times(1))
                .deleteJobProgressByReferenceId(HAPPY_BINDING_ID);
    }

}
