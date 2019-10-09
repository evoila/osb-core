package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomInstanceGroupConfig extends InstanceGroupConfig {

    protected String name;
    
    public Map<String, Object> consumes;

    public Map<String, Object> provides;

    public Map<String, Object> getProvides() { return provides; }

    public void setProvides(Map<String, Object> provides) { this.provides = provides; }

    public Map<String, Object> getConsumes() { return consumes; }

    public void setConsumes(Map<String, Object> consumes) { this.consumes = consumes; }

    public CustomInstanceGroupConfig() {
    }

    public CustomInstanceGroupConfig(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        CustomInstanceGroupConfig that = (CustomInstanceGroupConfig) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(consumes, that.consumes) &&
               Objects.equals(provides, that.provides);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, consumes, provides);
    }

}
