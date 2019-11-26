package de.evoila.cf.broker.service.impl.BindingServiceImplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstanceBinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class BindServiceTest extends BaseTest {

    @Test
    void createCredentialsThrows() throws ServiceBrokerException, InvalidParametersException, PlatformException {
        Exception[] exceptions = {
                new ServiceBrokerException(),
                new InvalidParametersException("Mock"),
                new PlatformException("Mock")
        };
        doThrow(exceptions)
                .when(service)
                .createCredentials(HAPPY_BINDING_ID,
                                   request,
                                   serviceInstance,
                                   plan,
                                   null);
        for (Exception expectedEx : exceptions) {
            Exception ex = assertThrows(expectedEx.getClass(),
                                        () -> service.bindService(HAPPY_BINDING_ID,
                                                                  request,
                                                                  serviceInstance,
                                                                  plan));
            assertSame(expectedEx, ex);
        }
    }

    @Nested
    class createCredentialsDoesNotThrow {

        @BeforeEach
        void setUp() throws ServiceBrokerException, InvalidParametersException, PlatformException {
            doReturn(HAPPY_CREDENTIALS)
                    .when(service)
                    .createCredentials(HAPPY_BINDING_ID,
                                       request,
                                       serviceInstance,
                                       plan,
                                       null);
            when(serviceInstance.getId())
                    .thenReturn(HAPPY_SERVICE_INSTANCE_ID);
        }

        private void testForServiceInstanceBinding(String expectedAppGuid) throws ServiceBrokerException, InvalidParametersException, PlatformException {
            ServiceInstanceBinding expectedBinding = new ServiceInstanceBinding(HAPPY_BINDING_ID,
                                                                                HAPPY_SERVICE_INSTANCE_ID,
                                                                                HAPPY_CREDENTIALS);
            expectedBinding.setAppGuid(expectedAppGuid);
            ServiceInstanceBinding binding = service.bindService(HAPPY_BINDING_ID,
                                                                 request,
                                                                 serviceInstance,
                                                                 plan);
            assertEquals(expectedBinding, binding);
        }

        @Nested
        class getBindResourceReturnsNull {

            @BeforeEach
            void setUp() {
                when(request.getBindResource())
                        .thenReturn(null);
            }

            @Test
            void getAppGuidOnRequestReturnsNull() throws ServiceBrokerException, InvalidParametersException, PlatformException {
                when(request.getAppGuid())
                        .thenReturn(null);
                testForServiceInstanceBinding(null);
            }

            @Test
            void getAppGuidOnRequestReturnsNotNull() throws ServiceBrokerException, InvalidParametersException, PlatformException {
                when(request.getAppGuid())
                        .thenReturn(HAPPY_APP_GUID);
                testForServiceInstanceBinding(HAPPY_APP_GUID);
            }

        }

        @Nested
        class getBindResourceReturnsNotNull {

            @BeforeEach
            void setUp() {
                when(request.getAppGuid())
                        .thenReturn(HAPPY_APP_GUID);
                when(request.getBindResource())
                        .thenReturn(bindResource);
            }

            @Test
            void getAppGuidOnBindResourceReturnsNull() throws ServiceBrokerException, InvalidParametersException, PlatformException {
                when(bindResource.getAppGuid())
                        .thenReturn(null);
                testForServiceInstanceBinding(HAPPY_APP_GUID);
            }

            @Test
            void getAppGuidOnBindResourceReturnsNotNull() throws ServiceBrokerException, InvalidParametersException, PlatformException {
                when(bindResource.getAppGuid())
                        .thenReturn(HAPPY_BIND_RESOURCE_APP_GUID);
                testForServiceInstanceBinding(HAPPY_BIND_RESOURCE_APP_GUID);
            }

        }

    }

}
