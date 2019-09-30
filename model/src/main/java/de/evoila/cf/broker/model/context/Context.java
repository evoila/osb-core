package de.evoila.cf.broker.model.context;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Context {

    public static final String PLATFORM_CLOUDFOUNDRY = "cloudfoundry";

    public static final String PLATFORM_KUBERNETES = "kubernetes";

    private static Logger log = LoggerFactory.getLogger(Context.class);

    @JsonProperty("platform")
    private String platform;

    @JsonProperty("organization_guid")
    private String organizationGuid;

    @JsonProperty("organization_name")
    private String organizationName;

    @JsonProperty("space_guid")
    private String spaceGuid;

    @JsonProperty("space_name")
    private String spaceName;

    @JsonProperty("instance_name")
    private String instanceName;

    @JsonProperty("clusterid")
    private String clusterId;

    @JsonProperty("namespace")
    private String namespace;

    private Map<String, Object> additionalFields;


    public Context() {
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        switch (platform) {
            case PLATFORM_CLOUDFOUNDRY:
            case PLATFORM_KUBERNETES:
                break;
            default:
                throw new IllegalArgumentException("Only Cloudfoundry and Kubernetes are supported Platforms");
        }

        this.platform = platform;
    }

    public String getOrganizationGuid() {
        return organizationGuid;
    }

    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getSpaceGuid() {
        return spaceGuid;
    }

    public void setSpaceGuid(String spaceGuid) {
        this.spaceGuid = spaceGuid;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    @JsonAnySetter
    public void setAdditionalFields(String key, Object value) {
        if (additionalFields == null) {
            additionalFields = new HashMap<>();
        }

        this.additionalFields.put(key, value);
    }

    public void validateContextObject() {
        switch (this.getPlatform()) {
            case PLATFORM_CLOUDFOUNDRY:
                validateCloudfoundryContextObject();
                break;
            case PLATFORM_KUBERNETES:
                validateKubernetesContextObject();
        }

    }

    private void validateKubernetesContextObject() {
        if (fieldIsPresent(this.getNamespace()) && fieldIsPresent(this.getClusterId())) {
            log.debug("Received a valid Kubernetes context object.");
        } else {
            throw new IllegalArgumentException("A Kubernetes Context object should contain the fields namespace and clusterId.");
        }
    }

    private void validateCloudfoundryContextObject() {
        if (fieldIsPresent(this.getOrganizationGuid()) &&
                fieldIsPresent(this.getOrganizationGuid()) &&
                fieldIsPresent(this.getOrganizationName()) &&
                fieldIsPresent(this.getSpaceGuid()) &&
                fieldIsPresent(this.getSpaceName()) &&
                fieldIsPresent(this.getInstanceName())
        ) {
            log.debug("Received a valid Cloudfoundry context object.");
        } else {
            throw new IllegalArgumentException("A valid Cloudfoundry context object should contain organization_guid, organization_name, space_guid, space_name and instance_name");
        }
    }

    private boolean fieldIsPresent(String value) {
        return Optional.ofNullable(value).map(s -> !s.isEmpty()).orElse(false);
    }
}
