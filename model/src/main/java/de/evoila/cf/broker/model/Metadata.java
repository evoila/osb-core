package de.evoila.cf.broker.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by reneschollmeyer, evoila on 16.03.18.
 */
public class Metadata extends InstanceGroupConfig {

    protected String ingressInstanceGroup;

    private List<CustomInstanceGroupConfig> instanceGroupConfig= new ArrayList<>();

    private Map<String, Object> customParameters = new HashMap<>();

    public Metadata() {}

    public Metadata(int connections, int nodes, String vmType, String persistentDiskType, List<NetworkReference> networks,
                    List<CustomInstanceGroupConfig> instanceGroupConfig, Map<String, Object> customParameters) {
        this.connections = connections;
        this.nodes = nodes;
        this.vmType = vmType;
        this.persistentDiskType = persistentDiskType;
        this.networks = networks;
        this.instanceGroupConfig = instanceGroupConfig;
        this.customParameters = customParameters;
    }

    public String getIngressInstanceGroup() {
        return ingressInstanceGroup;
    }

    public void setIngressInstanceGroup(String ingressInstanceGroup) {
        this.ingressInstanceGroup = ingressInstanceGroup;
    }

    public List<CustomInstanceGroupConfig> getInstanceGroupConfig() {
        return instanceGroupConfig;
    }

    public void setInstanceGroupConfig(List<CustomInstanceGroupConfig> instanceGroupConfig) {
        this.instanceGroupConfig = instanceGroupConfig;
    }

    public Map<String, Object> getCustomParameters() {
        return customParameters;
    }

    public void setCustomParameters(Map<String, Object> customParameters) {
        this.customParameters = customParameters;
    }
}
