package de.evoila.cf.config.security.uaa.utils;

/**
 * @author Marco Di Martino.
 */

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeaderCheckFilter extends GenericFilterBean {

    private static final String X_BROKER_API_VERSION = "x-broker-api-version";
    private static final String VERSION = "2.14";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getHeader(X_BROKER_API_VERSION) == null) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json");
            httpResponse.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Requests to Service Broker must contain header that declares API-version");
            return;
        } else if (!(req.getHeader(X_BROKER_API_VERSION).equals(VERSION))) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json");
            httpResponse.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Expected API-version: "+ VERSION + ", but found API-version:"+req.getHeader("x-broker-api-version"));
            return;
        }

        chain.doFilter(request, response);
    }

}
