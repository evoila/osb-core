package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Johannes Hiemer
 */
public class BaseServiceInstanceBindingResponse {

    @JsonIgnore
    protected boolean async;

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        async = async;
    }
}
