package de.evoila.cf.broker.interceptor;


import de.evoila.cf.broker.bean.CloudFoundryApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * ServiceInstancePermissionInterceptor
 * Interceptor which is checking the users permissions against the specified service instance id.
 *
 * @author latzinger
 */
public class ServiceInstancePermissionInterceptor implements HandlerInterceptor {

    private static final String USER_ID = "user_id";
    private static final String SUB = "sub";
    private static final String READ = "read";
    private static final String MANAGE = "manage";

    private final CloudFoundryApplicationProperties cloudFoundryApplicationProperties;

    private static final String CF_PERMISSIONS_ENDPOINT = "/v2/service_instances/:guid/permissions";

    public ServiceInstancePermissionInterceptor(CloudFoundryApplicationProperties cloudFoundryApplicationProperties) {
        this.cloudFoundryApplicationProperties = cloudFoundryApplicationProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) request.getUserPrincipal();
        Map<Object, Object> attributes = (Map<Object, Object>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String serviceInstanceId = (String) attributes.get("serviceInstanceId");

        if (!cannAccessServiceInstance(serviceInstanceId)) {
            throw new AuthenticationServiceException("User is not authorised to access" + serviceInstanceId + ". Please contact your System Administrator.");
        }

        return true;
    }

    private boolean cannAccessServiceInstance(String serviceInstanceId) {
        ResponseEntity<Map> permissions = fetchPermissions(serviceInstanceId);
        return (boolean) permissions.getBody().get(READ);
    }

    private ResponseEntity<Map> fetchPermissions(String serviceInstanceId) {
        HttpEntity<String> httpEntity = new HttpEntity<>(getHttpHeaders());
        String uri = CF_PERMISSIONS_ENDPOINT.replace(":guid", serviceInstanceId);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(cloudFoundryApplicationProperties.getCfApi()));

        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Map.class);

        if (response.getStatusCode().isError()) {
            throw new AuthenticationServiceException("Failed to request permissions for " + serviceInstanceId + ".");
        }

        return response;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getUserToken());
        return headers;
    }

    private String getUserId() {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return jwtAuthenticationToken.getToken().containsClaim(USER_ID) ? jwtAuthenticationToken.getToken().getClaimAsString(USER_ID) : jwtAuthenticationToken.getToken().getClaim(SUB);
    }

    private String getUserToken() {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return jwtAuthenticationToken.getToken().getTokenValue();
    }


}