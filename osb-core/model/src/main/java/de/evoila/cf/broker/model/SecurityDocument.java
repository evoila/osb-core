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

    @Expose
    @SerializedName("members")
    private NamesAndRoles members;

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



}