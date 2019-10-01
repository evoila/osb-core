package de.evoila.cf.broker.model.context;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    /**
     * throws IllegalArgumentException to force jackson to fail when deserializing a invalid context object according to osb-api-spec, and if platform is empty or null.
     */
    public void validateContextObject() {
        if (StringUtils.isEmpty(this.getPlatform())) {
            throw new IllegalArgumentException("no value for Platform found!");
        }

        switch (this.getPlatform()) {
            case PLATFORM_CLOUDFOUNDRY:
                validateCloudfoundryContextObject();
                break;
            case PLATFORM_KUBERNETES:
                validateKubernetesContextObject();
                break;
            case "null":
                throw new IllegalArgumentException("Platform " + this.getPlatform() + " is not supported. Use kubernetes or cloudfoundry");
        }
    }

    /**
     * Throws IllegalArgumentException to force jackson to fail when deserializing a invalid kubernetes context object according to osb-api-spec.*
     */
    private void validateKubernetesContextObject() {
        if (fieldIsPresent(this.getNamespace()) && fieldIsPresent(this.getClusterId())) {
            log.debug("Received a valid Kubernetes context object.");
        } else {
            throw new IllegalArgumentException("A Kubernetes Context object should contain the fields namespace and clusterId.");
        }
    }

    /**
     * Throws IllegalArgumentException to force jackson to fail when de serializing a invalid cloudfoundry context object according to osb-api-spec.*
     */
    private void validateCloudfoundryContextObject() {
        if (fieldIsPresent(this.getOrganizationGuid()) &&
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
        return !StringUtils.isEmpty(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Context context = (Context) o;
        return Objects.equals(platform, context.platform) &&
                Objects.equals(organizationGuid, context.organizationGuid) &&
                Objects.equals(organizationName, context.organizationName) &&
                Objects.equals(spaceGuid, context.spaceGuid) &&
                Objects.equals(spaceName, context.spaceName) &&
                Objects.equals(instanceName, context.instanceName) &&
                Objects.equals(clusterId, context.clusterId) &&
                Objects.equals(namespace, context.namespace) &&
                Objects.equals(additionalFields, context.additionalFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform, organizationGuid, organizationName, spaceGuid, spaceName, instanceName, clusterId, namespace, additionalFields);
    }
}
