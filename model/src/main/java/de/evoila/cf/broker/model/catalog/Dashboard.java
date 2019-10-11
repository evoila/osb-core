/**
 *
 */
package de.evoila.cf.broker.model.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Dashboard dashboard = (Dashboard) o;
        return Objects.equals(url, dashboard.url) &&
               Objects.equals(authEndpoint, dashboard.authEndpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, authEndpoint);
    }

}
