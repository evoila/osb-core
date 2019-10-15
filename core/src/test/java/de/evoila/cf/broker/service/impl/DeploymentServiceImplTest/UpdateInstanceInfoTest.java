package de.evoila.cf.broker.service.impl.DeploymentServiceImplTest;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UpdateInstanceInfoTest extends BaseTest {

    @Test
    void updateIsCalled() {
        service.updateInstanceInfo(serviceInstance);
        verify(serviceInstanceRepository, times(1))
                .updateServiceInstance(serviceInstance);
    }

}
