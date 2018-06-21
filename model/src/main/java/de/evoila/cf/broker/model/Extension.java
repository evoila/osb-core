package de.evoila.cf.broker.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by reneschollmeyer, evoila on 21.06.18.
 */
public class Extension {

    private List<Map<String, Object>> extension_apis;

    public Extension(String discoveryUrl, String tokenUrl) {
        Map<String, String> credentialValues = new HashMap<>();
        credentialValues.put("tokenURL", tokenUrl);

        List<Map<String, String>> credentials = new ArrayList<>();
        credentials.add(credentialValues);

        Map<String, Object> extensions = new HashMap<>();
        extensions.put("discovery_url", discoveryUrl);
        extensions.put("credentials", credentials);

        extension_apis = new ArrayList<>();
        extension_apis.add(extensions);
    }

    public List<Map<String, Object>> getExtensionApis() {
        return extension_apis;
    }
}
