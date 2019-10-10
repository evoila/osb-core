package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BaseServiceInstanceBindingResponse that = (BaseServiceInstanceBindingResponse) o;
        return async == that.async &&
               Objects.equals(originatingUser, that.originatingUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(async, originatingUser);
    }

}
