package de.evoila.cf.security.credentials;

import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.credential.CertificateCredential;
import de.evoila.cf.broker.model.credential.PasswordCredential;
import de.evoila.cf.broker.model.credential.UsernamePasswordCredential;
import org.springframework.credhub.support.certificate.CertificateParameters;

import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
public interface CredentialStore {

    UsernamePasswordCredential createUser(ServiceInstance serviceInstance, String valueName);

    UsernamePasswordCredential createUser(String instanceId, String valueName);

    UsernamePasswordCredential createUser(ServiceInstance serviceInstance, String valueName, String username);

    UsernamePasswordCredential createUser(String instanceId, String valueName, String username);

    UsernamePasswordCredential createUser(ServiceInstance serviceInstance, String valueName, String username, int passwordLength);

    UsernamePasswordCredential createUser(ServiceInstance serviceInstance, String valueName, String username, String password);

    UsernamePasswordCredential createUser(String instanceId, String valueName, String username, String password);

    UsernamePasswordCredential createUser(String instanceId, String valueName, String username, int passwordLength);

    UsernamePasswordCredential getUser(ServiceInstance serviceInstance, String valueName);

    UsernamePasswordCredential getUser(String instanceId, String valueName);

    PasswordCredential createPassword(ServiceInstance serviceInstance, String valueName);

    PasswordCredential createPassword(String instanceId, String valueName);

    PasswordCredential createPassword(ServiceInstance serviceInstance, String valueName, int passwordLength);

    PasswordCredential createPassword(String instanceId, String valueName, int passwordLength);

    String getPassword(ServiceInstance serviceInstance, String valueName);

    String getPassword(String instanceId, String valueName);

    Map<String, Object> createJson(ServiceInstance serviceInstance, String valueName, Map<String, Object> values);

    Map<String, Object> createJson(String instanceId, String valueName, Map<String, Object> values);

    Map<String, Object> getJson(ServiceInstance serviceInstance, String valueName, String key);

    Map<String, Object> getJson(String instanceId, String valueName, String key);

    void deleteCredentials(ServiceInstance serviceInstance, String valueName);

    void deleteCredentials(String instanceId, String valueName);

    CertificateCredential createCertificate(ServiceInstance serviceInstance, String valueName, CertificateParameters certificateParameters);

    CertificateCredential createCertificate(String instanceId, String valueName, CertificateParameters certificateParameters);

    void deleteCertificate(ServiceInstance serviceInstance, String valueName);

    void deleteCertificate(String instanceId, String valueName);

    CertificateCredential getCertificate(ServiceInstance serviceInstance, String value);

    CertificateCredential getCertificate(String instanceId, String valueName);
}
