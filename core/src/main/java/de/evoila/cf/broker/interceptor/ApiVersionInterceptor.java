package de.evoila.cf.broker.interceptor;

import de.evoila.cf.broker.model.annotations.ApiVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/** @author Marco Di Martino
 *
 * This class is used to intercept every request before it is sent to
 * the corresponding handler (controller). It checks whether the handling method
 * has an API-Version annotation that denote the values under which the method can be called.
 * If the request does not provide one of these values in the header, the handler will not be called
 * and the request will be rejected with a <i> Precondition Failed</i>
 *
 * */
public class ApiVersionInterceptor implements HandlerInterceptor {

    private static final String XBrokerAPIVersion = "X-Broker-API-Version";
    Logger log = LoggerFactory.getLogger(ApiVersionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (handler instanceof ResourceHttpRequestHandler ||
            handler instanceof ParameterizableViewController) {

            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        log.info("Intercepting on method " + method.getMethod().getName());

        if (!method.hasMethodAnnotation(ApiVersion.class)) {
            return true;
        }
        // Ignore warning because we check for Annotation existence in the previous if-statement
        //noinspection ConstantConditions
        String[] api = method.getMethodAnnotation(ApiVersion.class).value();
        ArrayList<String> apiVersions = new ArrayList<>(Arrays.asList(api));
        log.info("Intercepted request: " + request.getRequestURI());
        return checkApiVersion(apiVersions, request, response);
    }

    private boolean checkApiVersion(ArrayList<String> apis, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestApiVersion = request.getHeader(XBrokerAPIVersion);
        if (requestApiVersion == null){
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
                               "\"Requests to Service Broker must contain header that declares API-version\"");
            log.info("Intercepted a request without an X-Broker-API-Version header.");
            return false;
        }

        if (!(apis.contains(requestApiVersion))) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
                               "\"Header X-Broker-API-Version with value " + request.getHeader(XBrokerAPIVersion ) + " is not allowed on this request\"");
            log.info("Intercepted a request with a non-matching X-Broker-API-Version header (received "+requestApiVersion+" but method supports "+apis.toString()+").");
            return false;
        }
        return true;
    }
}
