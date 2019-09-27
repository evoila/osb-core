package de.evoila.cf.broker.model.catalog.plan;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InstanceGroupConfig {

    protected Integer connections;

    protected Integer nodes;

    protected String vmType;

    protected String persistentDiskType;

    protected Map<String, Object> properties;

    protected List<NetworkReference> networks;

    protected List<String> azs;

    public InstanceGroupConfig() {
    }

    public InstanceGroupConfig(Integer connections, Integer nodes, String vmType, String persistentDiskType, Map<String, Object> properties, List<NetworkReference> networks, List<String> azs) {
        this.connections = connections;
        this.nodes = nodes;
        this.vmType = vmType;
        this.persistentDiskType = persistentDiskType;
        this.properties = properties;
        this.networks = networks;
        this.azs = azs;
    }

    public Integer getConnections() {
        return connections;
    }

    public void setConnections(Integer connections) {
        this.connections = connections;
    }

    public Integer getNodes() {
        return nodes;
    }

    public void setNodes(Integer nodes) {
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<String> getAzs() {
        return azs;
    }

    public void setAzs(List<String> azs) {
        this.azs = azs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        InstanceGroupConfig that = (InstanceGroupConfig) o;
        return Objects.equals(connections, that.connections) &&
               Objects.equals(nodes, that.nodes) &&
               Objects.equals(vmType, that.vmType) &&
               Objects.equals(persistentDiskType, that.persistentDiskType) &&
               Objects.equals(properties, that.properties) &&
               Objects.equals(networks, that.networks) &&
               Objects.equals(azs, that.azs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connections, nodes, vmType, persistentDiskType, properties, networks, azs);
    }

}
