package de.evoila.cf.broker.model.catalog.plan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkReference {

    private String name;

    @JsonProperty("static_ips")
    private List<String> staticIps;

    private List<String> defaultNetwork;

    public NetworkReference() {
    }

    public NetworkReference(String network, List<String> staticIps, List<String> defaultNetwork) {
        this.name = network;
        this.staticIps = staticIps;
        this.defaultNetwork = defaultNetwork;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getStaticIps() {
        if (staticIps == null)
            staticIps = new ArrayList<>();
        return staticIps;
    }

    public void setStaticIps(List<String> staticIps) {
        this.staticIps = staticIps;
    }

    public List<String> getDefault() { return defaultNetwork; }

    public void setDefault(List<String> defaultNetwork) { this.defaultNetwork = defaultNetwork; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        NetworkReference that = (NetworkReference) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(staticIps, that.staticIps) &&
               Objects.equals(defaultNetwork, that.defaultNetwork);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, staticIps, defaultNetwork);
    }

}
