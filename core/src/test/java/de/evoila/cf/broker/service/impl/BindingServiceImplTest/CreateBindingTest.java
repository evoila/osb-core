package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateBindingTest extends BaseTest {

    private void bindServiceThrows() throws ServiceBrokerException, InvalidParametersException, PlatformException {
        Exception[] exceptions = {
                new ServiceBrokerException(),
                new InvalidParametersException("Mock"),
                new PlatformException("Mock")
        };
        doThrow(exceptions)
                .when(service)
                .bindService(HAPPY_BINDING_ID,
                             request,
                             serviceInstance,
                             plan);
        for (Exception expectedEx : exceptions) {
            Exception ex = assertThrows(expectedEx.getClass(),
                                        () -> service.createBinding(HAPPY_BINDING_ID,
                                                                    serviceInstance,
                                                                    request,
                                                                    plan));
            assertSame(expectedEx, ex);
        }
    }

    private void bindServiceDoesNotThrow() throws ServiceBrokerException, InvalidParametersException, PlatformException {
        doReturn(serviceInstanceBinding)
                .when(service)
                .bindService(HAPPY_BINDING_ID,
                             request,
                             serviceInstance,
                             plan);
        when(serviceInstanceBinding.getCredentials())
                .thenReturn(HAPPY_CREDENTIALS);
        when(serviceInstanceBinding.getSyslogDrainUrl())
                .thenReturn(HAPPY_SYSLOG_DRAIN_URL);
        ServiceInstanceBindingResponse expectedResponse = new ServiceInstanceBindingResponse(serviceInstanceBinding);
        ServiceInstanceBindingResponse response = service.createBinding(HAPPY_BINDING_ID,
                                                                        serviceInstance,
                                                                        request,
                                                                        plan);
        verify(bindingRepository, times(1))
                .addInternalBinding(serviceInstanceBinding);
        assertEquals(expectedResponse, response);
    }

    @Nested
    class getBindResourceReturnsNull {

        @BeforeEach
        void setUp() {
            when(request.getBindResource())
                    .thenReturn(null);
        }

        @Test
        void bindServiceThrows() throws ServiceBrokerException, InvalidParametersException, PlatformException {
            CreateBindingTest.this.bindServiceThrows();
        }

        @Test
        void bindServiceDoesNotThrow() throws ServiceBrokerException, InvalidParametersException, PlatformException {
            CreateBindingTest.this.bindServiceDoesNotThrow();
        }

    }

    @Nested
    class getBindResourceReturnsNotNull {

        @BeforeEach
        void setUp() {
            when(request.getBindResource())
                    .thenReturn(bindResource);
        }

        @Nested
        class routeIsEmpty {

            @BeforeEach
            void setUp() {
                when(bindResource.getRoute())
                        .thenReturn(null);
            }

            @Test
            void bindServiceThrows() throws ServiceBrokerException, InvalidParametersException, PlatformException {
                CreateBindingTest.this.bindServiceThrows();
            }

            @Test
            void bindServiceDoesNotThrow() throws ServiceBrokerException, InvalidParametersException, PlatformException {
                CreateBindingTest.this.bindServiceDoesNotThrow();
            }

        }

        @Nested
        class routeIsNotEmpty {

            @BeforeEach
            void setUp() {
                when(bindResource.getRoute())
                        .thenReturn(HAPPY_ROUTE);
            }

            @Test
            void bindRouteReturnsNull() {
                ServiceBrokerException expectedEx = new ServiceBrokerException("Could not bind route.");
                doReturn(null)
                        .when(service)
                        .bindRoute(serviceInstance, HAPPY_ROUTE);
                ServiceBrokerException ex = assertThrows(ServiceBrokerException.class,
                                                         () -> service.createBinding(HAPPY_BINDING_ID,
                                                                                     serviceInstance,
                                                                                     request,
                                                                                     plan));
                assertEquals(expectedEx, ex);
            }

            @Test
            void bindRouteReturnsNotNull() throws ServiceBrokerException, InvalidParametersException, PlatformException {
                doReturn(routeBinding)
                        .when(service)
                        .bindRoute(serviceInstance, HAPPY_ROUTE);
                when(routeBinding.getRoute())
                        .thenReturn(HAPPY_ROUTE);
                ServiceInstanceBindingResponse expectedResponse = new ServiceInstanceBindingResponse(HAPPY_ROUTE);
                ServiceInstanceBindingResponse response = service.createBinding(HAPPY_BINDING_ID,
                                                                                serviceInstance,
                                                                                request,
                                                                                plan);
                verify(routeBindingRepository, times(1))
                        .addRouteBinding(routeBinding);
                assertEquals(expectedResponse, response);
            }

        }

    }

}
