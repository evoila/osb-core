package de.evoila.cf.broker.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * @author Johannes Hiemer.
 */
public class EnvironmentUtils {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentUtils.class);

    public static boolean isTestEnvironment(Environment environment) {
        boolean isTestEnvironment = false;

        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(
                env -> (env.equalsIgnoreCase(GlobalConstants.TEST_PROFILE)))) {
            isTestEnvironment = true;
        }

        return isTestEnvironment;
    }

    public static String replaceLastChar(String value) {
        if (value != null && value.length() > 1)
            return value.substring(0, value.length() - 1).concat("T");

        return value;
    }

    public static String replaceUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);

            if (url.getHost().indexOf(GlobalConstants.TEST_PROFILE) == -1) {
                URL newURL = new URL(url.getProtocol(),
                        url.getHost().replaceFirst("\\.", "-" + GlobalConstants.TEST_PROFILE + "."),
                        url.getPort(), url.getFile());
                urlStr = newURL.toString();
            }
        } catch(MalformedURLException ex) {
            logger.info("Exception replacing URL", ex);
        }

        return urlStr;
    }
}
