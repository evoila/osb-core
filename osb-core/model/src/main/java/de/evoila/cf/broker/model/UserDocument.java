package de.evoila.cf.broker.model;


import com.google.gson.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author Marco Di Martino
 */

public class UserDocument {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("roles")
    private ArrayList<String> roles = new ArrayList<>();

    @SerializedName("type")
    private String type;

    public UserDocument(String id, String username, String password, ArrayList<String> roles, String type){
        setId(id);
        setUsername(username);
        setPassword(password);
        setRoles(roles);
        setType(type);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
