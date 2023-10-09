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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import de.evoila.cf.broker.model.ApiVersions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestIdentityInterceptorTest {

    private static final String X_BROKER_API_REQUEST_IDENTITY   = "X-Broker-API-Request-Identity";
    private static final String X_BROKER_API_VERSION            = "X-Broker-API-Version";
    private static final String HAPPY_REQUEST_IDENTITY          = "84d7705e-5029-429c-9141-f2b4ea21b8c7";

    @Mock
    private Logger log;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HandlerMethod handler;
    @Mock
    private Method method;

    private RequestIdentityInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new RequestIdentityInterceptor();
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

        @Nested
        class withHandlerMethodAsHandler {

            @BeforeEach
            void setUp() {
                when(handler.getMethod())
                        .thenReturn(method);
                when(method.getName())
                        .thenReturn("Mock");
            }

            @Test
            void withoutApiVersion() {
                when(request.getHeader(X_BROKER_API_VERSION))
                        .thenReturn(null);
                boolean result = interceptor.preHandle(request,
                                                       null,
                                                       handler);
                assertTrue(result);
            }

            @Test
            void withApiVersion2_13() {
                when(request.getHeader(X_BROKER_API_VERSION))
                        .thenReturn(ApiVersions.API_213);
                boolean result = interceptor.preHandle(request,
                                                       null,
                                                       handler);
                assertTrue(result);
            }

            @Test
            void withApiVersion2_14() {
                when(request.getHeader(X_BROKER_API_VERSION))
                        .thenReturn(ApiVersions.API_214);
                boolean result = interceptor.preHandle(request,
                                                       null,
                                                       handler);
                assertTrue(result);
            }

        }

    }

    @Nested
    class withHandlingIdentity {

        @BeforeEach
        void setUp() {
            when(handler.getMethod())
                    .thenReturn(method);
            when(method.getName())
                    .thenReturn("Mock");
            when(request.getHeader(X_BROKER_API_VERSION))
                    .thenReturn(ApiVersions.API_215);
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

        @Nested
        class withIdentity {

            @BeforeEach
            void setUp() {
                when(request.getHeader(X_BROKER_API_REQUEST_IDENTITY))
                        .thenReturn(HAPPY_REQUEST_IDENTITY);
            }

            @Test
            void withSetHeaderThrowing() {
                RuntimeException expectedE = new RuntimeException("Mock");
                doThrow(expectedE)
                        .when(response)
                        .setHeader(X_BROKER_API_REQUEST_IDENTITY, HAPPY_REQUEST_IDENTITY);
                boolean result = interceptor.preHandle(request,
                                                       response,
                                                       handler);
                verify(log, times(1))
                        .info("Failed retrieving X-Broker-API-Request-Identity with Cause", expectedE);
                assertTrue(result);
            }

            @Test
            void withEmptyIdentity() {
                when(request.getHeader(X_BROKER_API_REQUEST_IDENTITY))
                        .thenReturn("");
                boolean result = interceptor.preHandle(request,
                                                       response,
                                                       handler);
                verify(response, times(1))
                        .setHeader(X_BROKER_API_REQUEST_IDENTITY, "");
                assertTrue(result);
            }

            @Test
            void withGUIDIdentity() {
                boolean result = interceptor.preHandle(request,
                                                       response,
                                                       handler);
                verify(response, times(1))
                        .setHeader(X_BROKER_API_REQUEST_IDENTITY, HAPPY_REQUEST_IDENTITY);
                assertTrue(result);
            }

        }

    }

}
