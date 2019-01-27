package de.evoila.cf.broker.model.volume;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Jannik Heyl.
 */
public class Device {

    @JsonProperty("volume_id")
    private String volumeId;

    @JsonProperty("mount_config")
    private MountConfig mountConfig;

    public Device() {}

    public Device(String volumeId, MountConfig mountConfig) {
        this.volumeId = volumeId;
        this.mountConfig = mountConfig;
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public MountConfig getMountConfig() {
        return mountConfig;
    }

    public void setMountConfig(MountConfig mountConfig) {
        this.mountConfig = mountConfig;
    }
}