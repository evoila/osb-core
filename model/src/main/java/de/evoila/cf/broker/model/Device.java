package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jannikheyl on 13.02.18.
 */
public class Device {
    private String volume_id;
    @JsonProperty("mount_config")
    private MountConfig mountConfig;

    public Device(String volume_id, MountConfig mountConfig) {
        this.volume_id = volume_id;
        this.mountConfig = mountConfig;
    }

    public Device() {
    }

    public String getVolume_id() {
        return volume_id;
    }

    public void setVolume_id(String volume_id) {
        this.volume_id = volume_id;
    }

    public MountConfig getMountConfig() {
        return mountConfig;
    }

    public void setMountConfig(MountConfig mountConfig) {
        this.mountConfig = mountConfig;
    }
}
