package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.JobProgressService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncBindingServiceImplTest {

    private AsyncBindingServiceImpl asyncBindingService;

    @Mock
    JobProgressService jobProgressService;
    @Mock
    JobProgress jobProgress;
    @Mock
    BindingServiceImpl bindingService;
    @Mock
    Plan plan;
    @Mock
    ServiceInstance serviceInstance;

    @BeforeEach
    void setUp() {
        asyncBindingService = new AsyncBindingServiceImpl(jobProgressService);
    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    class asyncCreateServiceInstanceBinding {

        @Mock
        ServiceInstanceBindingRequest serviceInstanceBindingRequest;

        @Test
        void syncCreateBindingThrowsException () throws ServiceBrokerException, InvalidParametersException, PlatformException {
            when(jobProgressService.startJob("Id3", "123456", "Creating binding..", JobProgress.BIND)).thenReturn(jobProgress);
            when(bindingService.syncCreateBinding("123456", serviceInstance, serviceInstanceBindingRequest, plan)).thenThrow(ServiceBrokerException.class);
            when(jobProgress.getId()).thenReturn("Id3");

            asyncBindingService.asyncCreateServiceInstanceBinding(bindingService, "123456", serviceInstance, serviceInstanceBindingRequest, plan, true, "Id3");

            verify(jobProgressService, times(1)).failJob("Id3", "Internal error during binding creation, please contact our support.");
            verify(jobProgressService, never()).succeedProgress("Id3", "Instance Binding successfully created");
        }

        @Test
        void syncCreateBindingSucceeds () throws ServiceBrokerException, InvalidParametersException, PlatformException {
            when(jobProgressService.startJob("Id3", "123456", "Creating binding..", JobProgress.BIND)).thenReturn(jobProgress);
            when(bindingService.syncCreateBinding("123456", serviceInstance, serviceInstanceBindingRequest, plan)).thenReturn(new ServiceInstanceBindingResponse());
            when(jobProgress.getId()).thenReturn("Id3");

            asyncBindingService.asyncCreateServiceInstanceBinding(bindingService, "123456", serviceInstance, serviceInstanceBindingRequest, plan, true, "Id3");

            verify(jobProgressService, never()).failJob("Id3", "Internal error during binding creation, please contact our support.");
            verify(jobProgressService, times(1)).succeedProgress("Id3", "Instance Binding successfully created");
        }
    }

    @Nested
    class asyncDeleteServiceInstanceBinding {

        @Test
        void syncDeleteServiceInstanceBindingThrowsException() {
            when(jobProgressService.startJob("Id3", "123456", "Deleting binding..", JobProgress.UNBIND)).thenReturn(jobProgress);
            doThrow(new NullPointerException()).when(bindingService).syncDeleteServiceInstanceBinding("123456", serviceInstance, plan);
            when(jobProgress.getId()).thenReturn("Id3");

            asyncBindingService.asyncDeleteServiceInstanceBinding(bindingService, "123456", serviceInstance, plan, "Id3");

            verify(jobProgressService, times(1)).failJob("Id3", "Internal error during binding deletion, please contact our support.");
        }

        @Test
        void syncDeleteServiceInstanceBindingSucceeds() {
            when(jobProgressService.startJob("Id3", "123456", "Deleting binding..", JobProgress.UNBIND)).thenReturn(jobProgress);

            asyncBindingService.asyncDeleteServiceInstanceBinding(bindingService, "123456", serviceInstance, plan, "Id3");

            verify(jobProgressService, never()).failJob("Id3", "Internal error during binding deletion, please contact our support.");
        }
    }
}