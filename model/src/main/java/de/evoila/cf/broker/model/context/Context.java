package de.evoila.cf.broker.model.context;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "platform"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Cloudfoundry.class, name = "cloudfoundry"),
        @JsonSubTypes.Type(value = Kubernetes.class, name = "kubernetes")
})
public abstract class Context {

    @JsonProperty("platform")
    private String platform;

    public Context() {
    }

    public Context(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
