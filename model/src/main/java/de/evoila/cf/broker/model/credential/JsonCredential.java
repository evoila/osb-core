package de.evoila.cf.broker.model.credential;

import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
public class JsonCredential extends Credential {

    private Map<String, Object> json;

    public JsonCredential() {}

    public JsonCredential(Map<String, Object> json) {
        this.json = json;
    }

    public JsonCredential(String id, Map<String, Object> json) {
        this.id = id;
        this.json = json;
    }

    public Map<String, Object> getJson() {
        return json;
    }

    public void setJson(Map<String, Object> json) {
        this.json = json;
    }
}
