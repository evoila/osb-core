package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Johannes Hiemer
 */
public class BaseServiceInstanceBindingResponse {

    @JsonIgnore
    protected boolean async;

    private String originatingUser;

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        async = async;
    }

    @JsonSerialize
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("user_id")
    public String getOriginatingUser() {
        return originatingUser;
    }

    public void setOriginatingUser(String originatingUser) {
        this.originatingUser = originatingUser;
    }
}
