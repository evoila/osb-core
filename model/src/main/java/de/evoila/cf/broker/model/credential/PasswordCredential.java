package de.evoila.cf.broker.model.credential;

/**
 * @author Johannes Hiemer.
 */
public class PasswordCredential extends Credential {

    protected String password;

    public PasswordCredential() {}

    public PasswordCredential(String password) {
        this.password = password;
    }

    public PasswordCredential(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
