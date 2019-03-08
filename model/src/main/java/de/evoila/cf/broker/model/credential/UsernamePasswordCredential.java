package de.evoila.cf.broker.model.credential;

/**
 * @author Johannes Hiemer.
 */
public class UsernamePasswordCredential extends PasswordCredential {

    private String username;

    public UsernamePasswordCredential() {}

    public UsernamePasswordCredential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UsernamePasswordCredential(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
