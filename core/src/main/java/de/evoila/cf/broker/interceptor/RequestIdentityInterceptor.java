package de.evoila.cf.broker.interceptor;

import de.evoila.cf.broker.model.ApiVersions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestIdentityInterceptor implements HandlerInterceptor {

    private static final String X_BROKER_API_REQUEST_IDENTITY = "X-Broker-API-Request-Identity";
    private static final String XBrokerAPIVersion = "X-Broker-API-Version";
    private final Logger log = LoggerFactory.getLogger(RequestIdentityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof ResourceHttpRequestHandler)) {
            HandlerMethod method = (HandlerMethod) handler;
            log.info("Intercepting on method " + method.getMethod().getName());

            String requestApiVersion = request.getHeader(XBrokerAPIVersion);
            if (requestApiVersion != null && requestApiVersion.equals(ApiVersions.API_215)) {
                handleOriginatingIdentity(request, response);
            }
        }

        return true;
    }

    private void handleOriginatingIdentity(HttpServletRequest request, HttpServletResponse response) {
        String requestIdentity = request.getHeader(X_BROKER_API_REQUEST_IDENTITY);
        if (requestIdentity != null) {
            try {
                log.info(X_BROKER_API_REQUEST_IDENTITY + ": " + requestIdentity);
                response.setHeader(X_BROKER_API_REQUEST_IDENTITY, requestIdentity);
            } catch (Exception e) {
                log.info("Failed retrieving X-Broker-API-Request-Identity with Cause", e);
            }
        }
    }
}
