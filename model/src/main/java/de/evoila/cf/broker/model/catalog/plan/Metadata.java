package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.evoila.cf.broker.model.catalog.BackupConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by reneschollmeyer, evoila on 16.03.18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metadata extends InstanceGroupConfig {

    private List<String> bullets;

    private List<Cost> costs;

    private BackupConfig backup;

    private String displayName;

    private String ingressInstanceGroup;

    private String egressInstanceGroup;

    private List<CustomInstanceGroupConfig> instanceGroupConfig = new ArrayList<>();

    private Map<String, Object> customParameters = new HashMap<>();

    private String endpointName;

    private boolean active = true;

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

    public List<String> getBullets() {
        return bullets;
    }

    public void setBullets(List<String> bullets) {
        this.bullets = bullets;
    }

    public List<Cost> getCosts() {
        return costs;
    }

    public void setCosts(List<Cost> costs) {
        this.costs = costs;
    }

    public BackupConfig getBackup() {
        return backup;
    }

    public void setBackup(BackupConfig backup) {
        this.backup = backup;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getEgressInstanceGroup() {
        return egressInstanceGroup;
    }

    public void setEgressInstanceGroup(String egressInstanceGroup) {
        this.egressInstanceGroup = egressInstanceGroup;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        Metadata metadata = (Metadata) o;
        return active == metadata.active &&
               Objects.equals(bullets, metadata.bullets) &&
               Objects.equals(costs, metadata.costs) &&
               Objects.equals(backup, metadata.backup) &&
               Objects.equals(displayName, metadata.displayName) &&
               Objects.equals(ingressInstanceGroup, metadata.ingressInstanceGroup) &&
               Objects.equals(egressInstanceGroup, metadata.egressInstanceGroup) &&
               Objects.equals(instanceGroupConfig, metadata.instanceGroupConfig) &&
               Objects.equals(customParameters, metadata.customParameters) &&
               Objects.equals(endpointName, metadata.endpointName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bullets, costs, backup, displayName, ingressInstanceGroup, egressInstanceGroup, instanceGroupConfig, customParameters, endpointName, active);
    }

}
