/**
 *
 */
package de.evoila.cf.broker.model.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Johannes Hiemer
 */
public class Dashboard {

    @JsonProperty("url")
    private String url;

    @JsonProperty("auth_endpoint")
    private String authEndpoint;

    public Dashboard() {
    }

    public Dashboard(String url, String authEndpoint) {
        this.url = url;
        this.authEndpoint = authEndpoint;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthEndpoint() {
        return authEndpoint;
    }

    public void setAuthEndpoint(String authEndpoint) {
        this.authEndpoint = authEndpoint;
    }

}
