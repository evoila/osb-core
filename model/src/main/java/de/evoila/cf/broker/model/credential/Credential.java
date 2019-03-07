package de.evoila.cf.broker.model.credential;

import de.evoila.cf.broker.model.BaseEntity;

/**
 * @author Johannes Hiemer.
 */
public class Credential implements BaseEntity<String> {

    protected String id;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
