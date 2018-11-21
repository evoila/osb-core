package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomInstanceGroupConfig extends InstanceGroupConfig {

    protected String name;
    
    public Map<String, Object> consumes;

    public Map<String, Object> provides;

    public Map<String, Object> getProvides() { return provides; }

    public void setProvides(Map<String, Object> provides) { this.provides = provides; }

    public Map<String, Object> getConsumes() { return consumes; }

    public void setConsumes(Map<String, Object> consumes) { this.consumes = consumes; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
