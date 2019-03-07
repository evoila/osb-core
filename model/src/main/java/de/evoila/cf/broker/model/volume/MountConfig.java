package de.evoila.cf.broker.model.volume;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Jannik Heyl.
 */
public class MountConfig {

    private String uid;

    private String gid;

    @JsonProperty("file_mode")
    private String fileMode; //gives permission on file e.g 777

    @JsonProperty("dir_mode")
    private String dirMode; // gibes permissions on directory eg 666

    private String username;

    private String password;

    private String source;

    private String target;

    public MountConfig() {}

    public MountConfig(String uid, String gid, String fileMode, String dirMode) {
        this.uid = uid;
        this.gid = gid;
        this.fileMode = fileMode;
        this.dirMode = dirMode;
    }

    public MountConfig(String file_mode, String dir_mode) {
        this(null, null, file_mode, dir_mode);
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

    public String getFileMode() {
        return fileMode;
    }

    public void setFileMode(String fileMode) {
        this.fileMode = fileMode;
    }

    public String getDirMode() {
        return dirMode;
    }

    public void setDirMode(String dirMode) {
        this.dirMode = dirMode;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}