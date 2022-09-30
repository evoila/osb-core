package de.evoila.cf.broker.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * Utility for debugging purposes.  Enable in the spring config. 
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public class LoggingInterceptor extends HandlerInterceptorAdapter {

	private final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
	 
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) {
 
		StringBuilder message = new StringBuilder();
		message.append(request.getMethod());
		message.append(" ");
		message.append(request.getRequestURL());
		message.append(" ");
		message.append(request.getContentType());
		message.append("\n\t");
		message.append("headers: \n\t");
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			message.append(headerName);
			message.append("=");
			Enumeration<String> headerValues = request.getHeaders(headerName);
			while(headerValues.hasMoreElements()) {
				String headerValue = headerValues.nextElement();
				message.append(headerValue);
				if (headerValues.hasMoreElements()) {
					message.append(", ");
				}
			}
			message.append("\n\t");
		}
		
		logger.info(message.toString());
		
		return true;
	}

}
