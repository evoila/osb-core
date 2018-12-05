package de.evoila.cf.broker.model.backup;

import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
public class CreateItem {

    public String name;

    public Map<String, Object> parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
