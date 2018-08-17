package de.evoila.cf.broker.model;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String username;

    private String password;

    private Map<String, Object> properties = new HashMap<>();

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, Map<String, Object> properties) {
        this.username = username;
        this.password = password;
        this.properties = properties;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
