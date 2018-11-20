package de.evoila.cf.broker.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.cf.broker.model.annotations.ApiVersion;
import de.evoila.cf.broker.service.impl.AsyncBindingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    Logger log = LoggerFactory.getLogger(AsyncBindingServiceImpl.class);

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        HandlerMethod method = (HandlerMethod) handler;
        String [] api ;
        boolean doesApiMatch = true;
        log.info("Intercepting on method "+method.getMethod().getName());

        if (method.hasMethodAnnotation(ApiVersion.class)){
            api = method.getMethodAnnotation(ApiVersion.class).value();

            ArrayList<String> apiVersions = null;
            if (api != null) {
                apiVersions = new ArrayList<>(Arrays.asList(api));
                log.info("Intercepted request: " + request.getRequestURI());
            }
            try{
                doesApiMatch = checkApiVersion(apiVersions, request, response);
            }catch(Exception e){
                throw new IOException();
            }
        }
        return doesApiMatch;
    }

    private boolean checkApiVersion(ArrayList<String> apis, HttpServletRequest request, HttpServletResponse response) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        String headerValueNotAllowed = "Header X-Broker-API-Version with value " + request.getHeader(XBrokerAPIVersion ) + " is not allowed on this request";
        String noHeaderFound = "Requests to Service Broker must contain header that declares API-version";


        String requestApiVersion = request.getHeader(XBrokerAPIVersion);

        if (requestApiVersion == null){
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, mapper.writeValueAsString(noHeaderFound));
            return false;
        }

        if (!(apis.contains(requestApiVersion))) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, mapper.writeValueAsString(headerValueNotAllowed));
            return false;
        }
        return true;
    }
}