package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(
      ignoreUnknown = true
)
public class NetworkReference {

    String name;

    @JsonProperty("static_ips")
    List<String> staticIps;

    List<String> defaultNetwork;

    public NetworkReference() { }

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
        if(staticIps == null)
            staticIps = new ArrayList<>();
        return staticIps;
    }

    public void setStaticIps(List<String> staticIps) {
        this.staticIps = staticIps;
    }

    public List<String> getDefault() { return defaultNetwork; }

    public void setDefault(List<String> defaultNetwork) { this.defaultNetwork = defaultNetwork; }
}
