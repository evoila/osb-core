package de.evoila.cf.broker.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import de.evoila.cf.broker.model.annotations.ApiVersion;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiVersionInterceptorTest {

    private static final String X_BROKER_API_VERSION = "X-Broker-API-Version";

    private static final String HAPPY_API_VERSION = "2.15";

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
    @Mock
    private ApiVersion apiVersion;

    private ApiVersionInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new ApiVersionInterceptor();
        // Set a mocked logger to avoid cluttered logs
        try {
            Field loggerField = interceptor.getClass().getDeclaredField("log");
            loggerField.setAccessible(true); // Make the field accessible
            loggerField.set(interceptor, log);
        } catch (Exception e) {
            throw new RuntimeException("Setting logger failed", e);
        }
    }

    @Nested
    class withoutCheckingApiVersion {

        @Test
        void withResourceHttpRequestHandler() throws IOException {
            boolean result = interceptor.preHandle(null,
                                                   null,
                                                   new ResourceHttpRequestHandler());
            assertTrue(result);
        }

        @Test
        void withParameterizableViewControllerAsHandler() throws IOException {
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
            void withoutApiVersionAnnotation() throws IOException {
                when(handler.hasMethodAnnotation(ApiVersion.class))
                        .thenReturn(false);
                boolean result = interceptor.preHandle(request,
                                                       null,
                                                       handler);
                assertTrue(result);
            }

        }

    }

    @Nested
    class withCheckingApiVersion {

        @BeforeEach
        void setUp() {
            when(handler.getMethod())
                    .thenReturn(method);
            when(method.getName())
                    .thenReturn("Mock");
            when(handler.hasMethodAnnotation(ApiVersion.class))
                    .thenReturn(true);
            when(handler.getMethodAnnotation(ApiVersion.class))
                    .thenReturn(apiVersion);
            when(apiVersion.value())
                    .thenReturn(new String[] {HAPPY_API_VERSION});
        }

        private void testForFalse(String errorBody) throws IOException {
            boolean result = interceptor.preHandle(request,
                                                   response,
                                                   handler);
            assertFalse(result);
            verify(response, times(1))
                    .setContentType("application/json");
            verify(response, times(1))
                    .setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            verify(response, times(1))
                    .sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
                               errorBody);

        }

        @Test
        void withNullRequestApiVersion() throws IOException {
            when(request.getHeader(X_BROKER_API_VERSION))
                    .thenReturn(null);
            testForFalse("\"Requests to Service Broker must contain header that declares API-version\"");
        }

        @Test
        void withRequestApiVersionNotAllowed() throws IOException {
            when(request.getHeader(X_BROKER_API_VERSION))
                    .thenReturn("Mock");
            testForFalse("\"Header X-Broker-API-Version with value Mock is not allowed on this request\"");
        }

        @Test
        void withRequestApiVersionAllowed() throws IOException {
            when(request.getHeader(X_BROKER_API_VERSION))
                    .thenReturn(HAPPY_API_VERSION);
            boolean result = interceptor.preHandle(request,
                                                   response,
                                                   handler);
            assertTrue(result);
        }

    }

    @Nested
    class withExceptionThrown {

        @BeforeEach
        void setUp() {
            when(handler.getMethod())
                    .thenReturn(method);
            when(method.getName())
                    .thenReturn("Mock");
            when(handler.hasMethodAnnotation(ApiVersion.class))
                    .thenReturn(true);
            when(handler.getMethodAnnotation(ApiVersion.class))
                    .thenReturn(apiVersion);
            when(apiVersion.value())
                    .thenReturn(new String[] {HAPPY_API_VERSION});
        }

        private void testForIOException() {
            assertThrows(IOException.class,
                         () -> interceptor.preHandle(request,
                                                     response,
                                                     handler));
            verify(response, times(1))
                    .setContentType("application/json");
            verify(response, times(1))
                    .setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
        }

        @Test
        void withSendErrorThrowingWithoutRequestApiVersion() throws IOException {
            when(request.getHeader(X_BROKER_API_VERSION))
                    .thenReturn(null);
            doThrow(new IOException())
                    .when(response)
                    .sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
                               "\"Requests to Service Broker must contain header that declares API-version\"");
            testForIOException();
        }

        @Test
        void withSendErrorThrowingWithRequestApiVersionNotAllowed() throws IOException {
            when(request.getHeader(X_BROKER_API_VERSION))
                    .thenReturn("Mock");
            doThrow(new IOException())
                    .when(response)
                    .sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
                               "\"Header X-Broker-API-Version with value Mock is not allowed on this request\"");
            testForIOException();
        }

    }

}
