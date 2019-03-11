package de.evoila.cf.security.credentials.database;

import de.evoila.cf.broker.bean.CredhubBean;
import de.evoila.cf.broker.model.EnvironmentUtils;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.credential.CertificateCredential;
import de.evoila.cf.broker.model.credential.JsonCredential;
import de.evoila.cf.broker.model.credential.PasswordCredential;
import de.evoila.cf.broker.model.credential.UsernamePasswordCredential;
import de.evoila.cf.broker.repository.CredentialRepository;
import de.evoila.cf.security.credentials.CredentialStore;
import de.evoila.cf.security.utils.RandomString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.env.Environment;
import org.springframework.credhub.support.certificate.CertificateParameters;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
@Service
@ConditionalOnMissingBean(CredhubBean.class)
public class DatabaseCredentialsClient implements CredentialStore {

    private static String SERVICE_BROKER_PREFIX = "sb-";

    private static String ENCRYPTION_KEY = "evoilaVollGeil";

    private RandomString usernameRandomString = new RandomString(10);

    private CryptoUtil cryptoUtil = new CryptoUtil();

    private Environment environment;

    private CredentialRepository credentialRepository;

    public DatabaseCredentialsClient(Environment environment, CredentialRepository credentialRepository) {
        this.environment = environment;
        this.credentialRepository = credentialRepository;
        if (EnvironmentUtils.isTestEnvironment(environment)) {
            SERVICE_BROKER_PREFIX += "test-";
        }
    }

    private String identifier(String instanceId, String valueName) {
        return SERVICE_BROKER_PREFIX + instanceId + "-" + valueName;
    }

    @Override
    public UsernamePasswordCredential createUser(ServiceInstance serviceInstance, String valueName) {
        return this.createUser(serviceInstance.getId(), valueName);
    }

    @Override
    public UsernamePasswordCredential createUser(String instanceId, String valueName) {
        return this.createUser(instanceId, valueName, new RandomString(10, false, true).nextString());
    }

    @Override
    public UsernamePasswordCredential createUser(ServiceInstance serviceInstance, String valueName, String username) {
        return this.createUser(serviceInstance.getId(), valueName, username);
    }

    @Override
    public UsernamePasswordCredential createUser(String instanceId, String valueName, String username) {
        return this.createUser(instanceId, valueName, username, 20);
    }

    @Override
    public UsernamePasswordCredential createUser(ServiceInstance serviceInstance, String valueName, String username, int passwordLength) {
        return createUser(serviceInstance.getId(), valueName, username, passwordLength);
    }

    @Override
    public UsernamePasswordCredential createUser(String instanceId, String valueName, String username, int passwordLength) {
        RandomString passwordRandomString = new RandomString(passwordLength);
        UsernamePasswordCredential usernamePasswordCredential = null;
        try {
            usernamePasswordCredential =
                    new UsernamePasswordCredential(identifier(instanceId, valueName),
                            username.toLowerCase(),
                            cryptoUtil.encrypt(ENCRYPTION_KEY, passwordRandomString.nextString()));
            credentialRepository.save(usernamePasswordCredential);
        } catch(Exception ex) {
            // TODO: Check how we handle that shit
        }

        return usernamePasswordCredential;
    }

    @Override
    public UsernamePasswordCredential getUser(ServiceInstance serviceInstance, String valueName) {
        return this.getUser(serviceInstance.getId(), valueName);
    }

    @Override
    public UsernamePasswordCredential getUser(String instanceId, String valueName) {
        return (UsernamePasswordCredential) credentialRepository.getById(identifier(instanceId, valueName));
    }

    @Override
    public PasswordCredential createPassword(ServiceInstance serviceInstance, String valueName) {
        return this.createPassword(serviceInstance.getId(), valueName);
    }

    @Override
    public PasswordCredential createPassword(ServiceInstance serviceInstance, String valueName, int passwordLength) {
        return this.createPassword(serviceInstance.getId(), valueName, passwordLength);
    }

    @Override
    public PasswordCredential createPassword(String instanceId, String valueName) {
        return this.createPassword(instanceId, valueName, 20);
    }

    @Override
    public PasswordCredential createPassword(String instanceId, String valueName, int passwordLength) {
        RandomString passwordRandomString = new RandomString(passwordLength);
        PasswordCredential passwordCredential = null;
        try {
            passwordCredential =
                    new PasswordCredential(identifier(instanceId, valueName),
                            cryptoUtil.encrypt(ENCRYPTION_KEY, passwordRandomString.nextString()));
            credentialRepository.save(passwordCredential);
        } catch(Exception ex) {
            // TODO: Check how we handle that shit
        }

        return passwordCredential;
    }

    @Override
    public String getPassword(ServiceInstance serviceInstance, String valueName) {
        return this.getPassword(serviceInstance.getId(), valueName);
    }

    @Override
    public String getPassword(String instanceId, String valueName) {
        return ((PasswordCredential) credentialRepository.getById(identifier(instanceId, valueName))).getPassword();
    }

    @Override
    public Map<String, Object> createJson(ServiceInstance serviceInstance, String valueName, Map<String, Object> values) {
        return this.createJson(serviceInstance.getId(), valueName, values);
    }

    @Override
    public Map<String, Object> createJson(String instanceId, String valueName, Map<String, Object> values) {
        JsonCredential jsonCredential = null;
        try {
            jsonCredential =
                    new JsonCredential(identifier(instanceId, valueName),
                            values);
            credentialRepository.save(jsonCredential);
        } catch(Exception ex) {
            // TODO: Check how we handle that shit
        }

        return jsonCredential.getJson();
    }

    @Override
    public Map<String, Object> getJson(ServiceInstance serviceInstance, String valueName, String key) {
        return this.getJson(serviceInstance.getId(), valueName, key);
    }

    @Override
    public Map<String, Object> getJson(String instanceId, String valueName, String key) {
        return ((JsonCredential) credentialRepository.getById(identifier(instanceId, valueName))).getJson();
    }

    @Override
    public void deleteCredentials(ServiceInstance serviceInstance, String valueName) {
        this.deleteCredentials(serviceInstance.getId(), valueName);
    }

    @Override
    public void deleteCredentials(String instanceId, String valueName) {
        credentialRepository.deleteById(identifier(instanceId, valueName));
    }

    @Override
    public CertificateCredential createCertificate(ServiceInstance serviceInstance, String valueName,
                                                   CertificateParameters certificateParameters) {
        return this.createCertificate(serviceInstance.getId(), valueName, certificateParameters);
    }

    @Override
    public CertificateCredential createCertificate(String instanceId, String valueName, CertificateParameters certificateParameters) {
        throw new UnsupportedOperationException("DataCredentialsClient does not support Certificate Management");
    }

    @Override
    public void deleteCertificate(ServiceInstance serviceInstance, String valueName) {
        this.deleteCertificate(serviceInstance.getId(), valueName);
    }

    @Override
    public void deleteCertificate(String instanceId, String valueName) {
        throw new UnsupportedOperationException("DataCredentialsClient does not support Certificate Management");
    }

    @Override
    public CertificateCredential getCertificate(ServiceInstance serviceInstance, String value) {
        return this.getCertificate(serviceInstance.getId(), value);
    }

    @Override
    public CertificateCredential getCertificate(String instanceId, String valueName) {
        throw new UnsupportedOperationException("DataCredentialsClient does not support Certificate Management");
    }
}
