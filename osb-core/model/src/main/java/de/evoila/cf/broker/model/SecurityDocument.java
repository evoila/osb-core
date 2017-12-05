package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Marco Di Martino
 */

public class SecurityDocument {

    @Expose
    @SerializedName("admins")
    private NamesAndRoles admins;

    /*@Expose(serialize=false)
    private transient ArrayList<String> ad_username = new ArrayList<>();

    @Expose(serialize=false, deserialize=false)
    private transient ArrayList<String> ad_roles = new ArrayList<>();
*/
    @Expose
    @SerializedName("members")
    private NamesAndRoles members;
/*
    @Expose(serialize=false)
    private transient ArrayList<String> mem_username = new ArrayList<>();

    @Expose(serialize=false)
    private transient ArrayList<String> mem_roles = new ArrayList<>();

    public SecurityDocument (ArrayList<String> ad_username, ArrayList<String> ad_roles, ArrayList<String> mem_username, ArrayList<String> mem_roles ) {
        setAd_username(ad_username);
        setAd_roles(ad_roles);
        setMem_username(mem_username);
        setMem_roles(mem_roles);
        setAdmins(ad_username, ad_roles);
        setMembers(mem_username, mem_roles);
    }
*/
    public SecurityDocument (NamesAndRoles admins, NamesAndRoles members) {
        setAdmins(admins);
        setMembers(members);
    }

    public NamesAndRoles getAdmins() {
        return admins;
    }

    public void setAdmins(NamesAndRoles admins) {
        this.admins = admins;
    }

    public NamesAndRoles getMembers() {
        return members;
    }

    public void setMembers(NamesAndRoles members) {
        this.members = members;
    }

    /*
    public Map<String, Object> getAdmins() {
        return this.admins;
    }

    public void setAdmins(ArrayList<String> username, ArrayList<String> roles) {
        this.admins = new LinkedHashMap<>();
        admins.put("names", username);
        admins.put("roles", roles);

    }

    public ArrayList<String> getAd_username() {
        return ad_username;
    }

    public void setAd_username(ArrayList<String> ad_username) {
        this.ad_username = ad_username;
    }

    public ArrayList<String> getAd_roles() {
        return ad_roles;
    }

    public void setAd_roles(ArrayList<String> ad_roles) {
        this.ad_roles = ad_roles;
    }

    public ArrayList<String> getMem_username() {
        return mem_username;
    }

    public void setMem_username(ArrayList<String> mem_username) {
        this.mem_username = mem_username;
    }

    public ArrayList<String> getMem_roles() {
        return mem_roles;
    }

    public void setMem_roles(ArrayList<String> mem_roles) {
        this.mem_roles = mem_roles;
    }

    public Map<String, Object> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> mem_username, ArrayList<String> mem_roles){
        this.members = new LinkedHashMap<>();
        members.put("names", mem_username);
        members.put("roles", mem_roles);
    }

    //public void add_User(String username){
        //getAd_username().add(username);
    //}
*/

}