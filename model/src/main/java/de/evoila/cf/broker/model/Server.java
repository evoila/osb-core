package de.evoila.cf.broker.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Server server = (Server) o;
        return Objects.equals(url, server.url) &&
               Objects.equals(identifier, server.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, identifier);
    }

}
