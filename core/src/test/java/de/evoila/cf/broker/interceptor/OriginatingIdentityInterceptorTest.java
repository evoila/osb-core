package de.evoila.cf.broker.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OriginatingIdentityInterceptorTest {

    @Mock
    private Logger log;

    private OriginatingIdentityInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new OriginatingIdentityInterceptor();
        // Set a mocked logger to avoid cluttered logs
        try {
            FieldSetter.setField(interceptor,
                                 interceptor.getClass().getDeclaredField("log"),
                                 log);
        } catch (Exception e) {
            throw new RuntimeException("Setting logger failed", e);
        }
    }

    @Nested
    class withoutHandlingIdentity {

        @Test
        void withResourceHttpRequestHandler() {
            boolean result = interceptor.preHandle(null,
                                                   null,
                                                   new ResourceHttpRequestHandler());
            assertTrue(result);
        }
        @Test
        void withParameterizableViewControllerAsHandler() {
            boolean result = interceptor.preHandle(null,
                                                   null,
                                                   new ParameterizableViewController());
            assertTrue(result);
        }

    }

    @Nested
    class withHandlingIdentity {

        private final String X_BROKER_API_REQUEST_IDENTITY = "X-Broker-API-Originating-Identity";

        @Mock
        private HttpServletRequest request;
        @Mock
        private HttpServletResponse response;
        @Mock
        private HandlerMethod handler;
        @Mock
        private Method method;

        @BeforeEach
        void setUp() {
            when(handler.getMethod())
                    .thenReturn(method);
            when(method.getName())
                    .thenReturn("Mock");
        }

        @Test
        void withoutIdentity() {
            when(request.getHeader(X_BROKER_API_REQUEST_IDENTITY))
                    .thenReturn(null);
            boolean result = interceptor.preHandle(request,
                                                   response,
                                                   handler);
            assertTrue(result);
        }

        /**
         * A valid identity consists of 2 parts separated by a space where the second part is base64 encoded.
         * E.g. "cloudfoundry eyANCiAgInVzZXJfaWQiOiAiNjgzZWE3NDgtMzA5Mi00ZmY0LWI2NTYtMzljYWNjNGQ1MzYwIg0KfQ=="
         */
        @Nested
        class withIdentity {

            @Nested
            class withMalformed {

                @Test
                void withoutSecondComponent() {
                    when(request.getHeader(X_BROKER_API_REQUEST_IDENTITY))
                            .thenReturn("mock");
                    boolean result = interceptor.preHandle(request,
                                                           response,
                                                           handler);
                    verifyZeroInteractions(response);
                    assertTrue(result);
                }

                @Test
                void withoutB64EncodingOnSecondComponent() {
                    when(request.getHeader(X_BROKER_API_REQUEST_IDENTITY))
                            .thenReturn("mock b69f9723-bd9c-4432-a603-fb0873650145");
                    boolean result = interceptor.preHandle(request,
                                                           response,
                                                           handler);
                    verifyZeroInteractions(response);
                    assertTrue(result);
                }

            }

            @Test
            void withOneSpace() {
                String HAPPY_ORIGINATING_ID = "cloudfoundry eyANCiAgInVzZXJfaWQiOiAiNjgzZWE3NDgtMzA5Mi00ZmY0LWI2NTYtMzljYWNjNGQ1MzYwIg0KfQ==";
                when(request.getHeader(X_BROKER_API_REQUEST_IDENTITY))
                        .thenReturn(HAPPY_ORIGINATING_ID);
                boolean result = interceptor.preHandle(request,
                                                       response,
                                                       handler);
                verify(response, times(1))
                        .setHeader(X_BROKER_API_REQUEST_IDENTITY, HAPPY_ORIGINATING_ID);
                verifyNoMoreInteractions(response);
                assertTrue(result);
            }

            @Test
            void withMoreThanOneSpace() {
                String HAPPY_ORIGINATING_ID = "cloudfoundry eyANCiAgInVzZXJfaWQiOiAiNjgzZWE3NDgtMzA5Mi00ZmY0LWI2NTYtMzljYWNjNGQ1MzYwIg0KfQ== 4sd89z9hu34r84wsu8s89";
                when(request.getHeader(X_BROKER_API_REQUEST_IDENTITY))
                        .thenReturn(HAPPY_ORIGINATING_ID);
                boolean result = interceptor.preHandle(request,
                                                       response,
                                                       handler);
                verify(response, times(1))
                        .setHeader(X_BROKER_API_REQUEST_IDENTITY, HAPPY_ORIGINATING_ID);
                verifyNoMoreInteractions(response);
                assertTrue(result);
            }

        }

    }

}
