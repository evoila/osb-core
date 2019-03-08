package de.evoila.cf.broker.model;

/**
 * @author Rene Schollmeyer.
 */
public class Server {

    private String url;

    private String identifier;

    public Server() {}

    public Server(String url, String identifier) {
        this.url = url;
        this.identifier = identifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
