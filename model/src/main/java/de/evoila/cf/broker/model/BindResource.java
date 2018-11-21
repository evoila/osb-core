package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.validation.constraints.NotEmpty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BindResource {

    @JsonSerialize
    @JsonProperty("app_guid")
    private String appGuid;

    @NotEmpty
    @JsonSerialize
    @JsonProperty("route")
    private String route;

    public String getAppGuid() {
        return appGuid;
    }

    public void setAppGuid(String appGuid) {
        this.appGuid = appGuid;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}