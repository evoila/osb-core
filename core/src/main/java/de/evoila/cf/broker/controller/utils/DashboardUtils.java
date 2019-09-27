/**
 * 
 */
package de.evoila.cf.broker.controller.utils;

import org.apache.http.util.TextUtils;

import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;

import java.net.URL;

/**
 * @author Johannes Hiemer.
 *
 */
public class DashboardUtils {
	
	public static boolean hasDashboard(ServiceDefinition serviceDefinition) {
		return (serviceDefinition != null
				&& serviceDefinition.getDashboard() != null
				&& serviceDefinition.getDashboard().getUrl() != null
				&& DashboardUtils.isURL(serviceDefinition.getDashboard().getUrl())
				&& serviceDefinition.getDashboardClient() != null);
	}
	
	public static String dashboard(ServiceDefinition serviceDefinition, String serviceInstanceId)
			throws IllegalArgumentException {
		if (serviceDefinition == null ||
			serviceDefinition.getDashboard() == null ||
			TextUtils.isEmpty(serviceInstanceId)) {

			throw new IllegalArgumentException();
		}
		return DashboardUtils.appendSegmentToPath(serviceDefinition.getDashboard().getUrl(), serviceInstanceId);
	}

	public static String redirectUri(DashboardClient dashboardClient, String... appendixes)
			throws IllegalArgumentException {
		if (dashboardClient == null ||
			appendixes == null) {

			throw new IllegalArgumentException();
		}
		String url = dashboardClient.getRedirectUri();
		for (String appendix : appendixes) {
			url = DashboardUtils.appendSegmentToPath(url, appendix);
		}

		return url;
	}

	public static boolean isURL(String url) {
	    try {
	        new URL(url);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}

	private static String prepareForUrlConcatenation(String string) {
		if (string == null) {
			return "";
		}
		string = string.trim();
		return string.replaceAll("^/|/$", "");
	}
	
	private static String appendSegmentToPath(String path, String segment) {
		path = prepareForUrlConcatenation(path);
		if (TextUtils.isEmpty(segment)) {
			return path;
		}
		segment = prepareForUrlConcatenation(segment);
		return path + "/" + segment;
	}

}
