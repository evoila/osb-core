package de.evoila.cf.broker.service.custom;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NamesAndRoles {

        @Expose
        @SerializedName("names")
        private ArrayList<String> names;

        @Expose
        @SerializedName("roles")
        private ArrayList<String> roles;

        public NamesAndRoles(ArrayList<String> names, ArrayList<String> roles){
            this.names = names;
            this.roles = roles;
        }


    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }
    public void addName(String name){
        getNames().add(name);
    }

    public void addRole(String role){
        getRoles().add(role);
    }
    public void deleteName(String name){
        getNames().remove(name);
    }
    public void deleteRole(String role){
        getRoles().remove(role);
    }
}


