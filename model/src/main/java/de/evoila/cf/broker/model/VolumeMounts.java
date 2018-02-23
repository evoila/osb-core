package de.evoila.cf.broker.model;

import java.io.File;

/**
 * Created by jannikheyl on 13.02.18.
 */
public class VolumeMounts {
    private String driver;
    private String container_dir;
    private VolumeMode mode;
    private DeviceType device_type;
    private Device device;

    public VolumeMounts(String driver, String container_dir, VolumeMode mode, DeviceType device_type, Device device) {
        this.driver = driver;
        this.container_dir = container_dir;
        this.mode = mode;
        this.device_type = device_type;
        this.device = device;
    }

    public VolumeMounts() {
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getContainer_dir() {
        return container_dir;
    }

    public void setContainer_dir(String container_dir) {
        this.container_dir = container_dir;
    }
    public VolumeMode getMode() {
        return mode;
    }

    public void setMode(VolumeMode mode) {
        this.mode = mode;
    }

    public DeviceType getDevice_type() {
        return device_type;
    }

    public void setDevice_type(DeviceType device_type) {
        this.device_type = device_type;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
