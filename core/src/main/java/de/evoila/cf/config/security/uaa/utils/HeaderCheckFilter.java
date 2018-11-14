package de.evoila.cf.config.security.uaa.utils;

/**
 * @author Marco Di Martino.
 */

import de.evoila.cf.broker.controller.core.CatalogController;
import de.evoila.cf.broker.controller.core.ServiceInstanceBindingController;
import de.evoila.cf.broker.controller.core.ServiceInstanceController;
import de.evoila.cf.broker.model.ApiVersion;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import org.springframework.web.filter.GenericFilterBean;

import javax.el.MethodNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class HeaderCheckFilter extends GenericFilterBean {

    private static final String X_BROKER_API_VERSION = "x-broker-api-version";

    private static final String PATTERN_FOR_CATALOG = "/v2/catalog[/]?";
    private static final String PATTERN_FOR_PROVISION = "/v2/service_instances/[A-Za-z0-9-]+[/]?";
    private static final String PATTERN_FOR_INSTANCE_LAST_OPERATION = "/v2/service_instances/[A-Za-z0-9-]+/last_operation[/]?";
    private static final String PATTERN_FOR_SERVICE_BINDING = "/v2/service_instances/[A-Za-z0-9-]+/service_bindings/[A-Za-z0-9-]+[/]?";
    private static final String PATTERN_FOR_BINDING_LAST_OPERATION = "/v2/service_instances/[A-Za-z0-9-]+/service_bindings/[A-Za-z0-9-]+/last_operation[/]?";

    private static final String INSTANCE_CONTROLLER = "instance";
    private static final String BINDING_CONTROLLER = "binding";
    private static final String POLL_INSTANCE = "polling_instance";
    private static final String POLL_BINDING= "polling_binding";
    private static final String CATALOG_CONTROLLER = "catalog";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getHeader(X_BROKER_API_VERSION) == null) {

            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json");
            httpResponse.sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
                    "Requests to Service Broker must contain header that declares API-version");
            return;
        }else {
            String[] list = getApiVersions(req);
            if (list == null){ // got no annotation on controller, no need to check version number (e.g. custom endpoints)
                return ;
            }
            ArrayList<String> version = new ArrayList<>(Arrays.asList(list));
            if (!(version.contains(req.getHeader(X_BROKER_API_VERSION)))) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setContentType("application/json");
                httpResponse.sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
                        "Header X-Broker-API-Version with value "+req.getHeader("x-broker-api-version")+" is now allowed on this request");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private String[] getApiVersions(HttpServletRequest request) {
        String [] versions = null;
        String controller = findMatching(request);
        String method = request.getMethod();
        try {
            if (controller == null){
                return null;
            }else if (controller.equals(INSTANCE_CONTROLLER)) {
                switch (method) {
                    case "GET":
                        versions = ServiceInstanceController.class.getMethod("fetchServiceInstance", String.class, String.class).getAnnotation(ApiVersion.class).value();break;
                    case "PUT":
                        versions = ServiceInstanceController.class.getMethod("createServiceInstance", String.class, Boolean.class, ServiceInstanceRequest.class).getAnnotation(ApiVersion.class).value();break;
                    case "DELETE":
                        versions = ServiceInstanceController.class.getMethod("deleteServiceInstance", String.class, Boolean.class, String.class, String.class).getAnnotation(ApiVersion.class).value();break;
                    case "PATCH":
                        versions = ServiceInstanceController.class.getMethod("updateServiceInstance", String.class, Boolean.class, ServiceInstanceRequest.class).getAnnotation(ApiVersion.class).value();break;
                }
            }else if (controller.equals(BINDING_CONTROLLER)) {
                switch (method) {
                    case "GET":
                        versions = ServiceInstanceBindingController.class.getMethod("fetchServiceInstanceBinding", String.class, String.class, String.class).getAnnotation(ApiVersion.class).value();break;
                    case "PUT":
                        versions = ServiceInstanceBindingController.class.getMethod("bindServiceInstance", String.class, String.class, String.class, Boolean.class, ServiceInstanceBindingRequest.class).getAnnotation(ApiVersion.class).value();break;
                    case "DELETE":
                        versions = ServiceInstanceBindingController.class.getMethod("deleteServiceInstanceBinding", String.class, String.class, String.class, String.class).getAnnotation(ApiVersion.class).value();break;
                }
            }else if (controller.equals(CATALOG_CONTROLLER)) {
                versions = CatalogController.class.getMethod("getCatalog").getAnnotation(ApiVersion.class).value();
            }else if (controller.equals(POLL_INSTANCE)){
                versions = ServiceInstanceController.class.getMethod("lastOperation", String.class ).getAnnotation(ApiVersion.class).value();
            }else if (controller.equals(POLL_BINDING)){
                versions = ServiceInstanceBindingController.class.getMethod("lastOperation", String.class, String.class, String.class, String.class, String.class).getAnnotation(ApiVersion.class).value();
            }
        }catch (Exception e){
            throw new MethodNotFoundException("Request seems to be malformed or trying to access a method that is not present.");
        }
        return versions;
   }

    private String findMatching(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String controller ;
        if (uri.matches(PATTERN_FOR_PROVISION)) {
            controller = INSTANCE_CONTROLLER;
        }else if (uri.matches(PATTERN_FOR_CATALOG)) {
            controller = CATALOG_CONTROLLER;
        }else if (uri.matches(PATTERN_FOR_SERVICE_BINDING)) {
            controller = BINDING_CONTROLLER;
        }else if (uri.matches(PATTERN_FOR_INSTANCE_LAST_OPERATION)) {
            controller = POLL_INSTANCE;
        }else if (uri.matches(PATTERN_FOR_BINDING_LAST_OPERATION)) {
            controller = POLL_BINDING;
        }else {
            controller = null;
        }
        return controller;
    }
}

