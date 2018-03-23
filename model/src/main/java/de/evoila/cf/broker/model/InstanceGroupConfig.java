package de.evoila.cf.broker.model;

import java.util.List;

public class InstanceGroupConfig {

    protected int connections;

    protected int nodes;

    protected String vmType;

    protected String persistentDiskType;

    protected List<NetworkReference> networks;

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public int getNodes() {
        return nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public String getVmType() {
        return vmType;
    }

    public void setVmType(String vmType) {
        this.vmType = vmType;
    }

    public String getPersistentDiskType() {
        return persistentDiskType;
    }

    public void setPersistentDiskType(String persistentDiskType) {
        this.persistentDiskType = persistentDiskType;
    }

    public List<NetworkReference> getNetworks() {
        return networks;
    }

    public void setNetworks(List<NetworkReference> networks) {
        this.networks = networks;
    }

}
