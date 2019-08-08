package de.evoila.cf.broker.interceptor;

import de.evoila.cf.broker.model.ApiVersions;
import de.evoila.cf.broker.model.annotations.ApiVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class OriginatingIdentityInterceptor implements HandlerInterceptor {

    private static final String X_BROKER_API_REQUEST_IDENTITY = "X-Broker-API-Request-Identity";
    private final Logger log = LoggerFactory.getLogger(OriginatingIdentityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof ResourceHttpRequestHandler)) {
            HandlerMethod method = (HandlerMethod) handler;
            String[] api;
            log.info("Intercepting on method " + method.getMethod().getName());

            if (method.hasMethodAnnotation(ApiVersion.class)) {
                api = Objects.requireNonNull(method.getMethodAnnotation(ApiVersion.class)).value();
                ArrayList<String> apiVersions = new ArrayList<>(Arrays.asList(api));

                if (apiVersions.contains(ApiVersions.API_215)) {
                    handleOriginatingIdentity(request, response);
                }
            }
        }
        return true;
    }

    private void handleOriginatingIdentity(HttpServletRequest request, HttpServletResponse response) {
        String originatingIdentity = request.getHeader(X_BROKER_API_REQUEST_IDENTITY);

        if (originatingIdentity != null) {
            response.setHeader(X_BROKER_API_REQUEST_IDENTITY, originatingIdentity);
            log.info(X_BROKER_API_REQUEST_IDENTITY + ": " + originatingIdentity);
        }
    }
}
