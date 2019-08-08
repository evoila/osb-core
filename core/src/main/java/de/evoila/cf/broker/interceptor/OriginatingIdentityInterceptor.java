package de.evoila.cf.broker.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

public class OriginatingIdentityInterceptor implements HandlerInterceptor {

    private static final String X_BROKER_API_REQUEST_IDENTITY = "X-Broker-API-Originating-Identity";
    private final Logger log = LoggerFactory.getLogger(OriginatingIdentityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof ResourceHttpRequestHandler)) {
            HandlerMethod method = (HandlerMethod) handler;
            log.info("Intercepting on method " + method.getMethod().getName());
            handleOriginatingIdentity(request, response);
        }

        return true;
    }

    private void handleOriginatingIdentity(HttpServletRequest request, HttpServletResponse response) {
        String originatingIdentity = request.getHeader(X_BROKER_API_REQUEST_IDENTITY);

        if (originatingIdentity != null) {
            try {
                String[] splitter = originatingIdentity.split(" ");
                String decodedValue = new String(Base64.getDecoder().decode(splitter[1]));
                log.info(X_BROKER_API_REQUEST_IDENTITY + " Platform " + splitter[0] + " and Value: " + decodedValue);
                response.setHeader(X_BROKER_API_REQUEST_IDENTITY, originatingIdentity);
            } catch (Exception e) {
                log.info("Failed retrieving X-Broker-API-Originating-Identity with Cause", e);
            }
        }
    }
}
