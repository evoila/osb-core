package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by jannikheyl on 13.02.18.
 */
public class MountConfig {
    private String uid;
    private String gid;
    private String file_mode; //gives permission on file e.g 777
    private String dir_mode; // gibes permissions on directory eg 666
    //@JsonInclude(JsonInclude.Include.NON_EMPTY)
    //private boolean readonly;
    private String username;
    private String password;
    private String source;


    public MountConfig(String uid, String gid, String file_mode, String dir_mode) {
        this.uid = uid;
        this.gid = gid;
        this.file_mode = file_mode;
        this.dir_mode = dir_mode;
    }
    public MountConfig(String file_mode, String dir_mode){
        this(null, null, file_mode, dir_mode);
    }
    public MountConfig(){

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getFile_mode() {
        return file_mode;
    }

    public void setFile_mode(String file_mode) {
        this.file_mode = file_mode;
    }

    public String getDir_mode() {
        return dir_mode;
    }

    public void setDir_mode(String dir_mode) {
        this.dir_mode = dir_mode;
    }

    /*public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }*/

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
