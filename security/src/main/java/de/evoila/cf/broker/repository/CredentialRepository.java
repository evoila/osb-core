package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.credential.Credential;

/**
 * @author Johannes Hiemer.
 */
public interface CredentialRepository {

    void save(Credential credential);

    Credential getById(String identifier);

    void deleteById(String identifier);

    void delete(Credential credential);
}
