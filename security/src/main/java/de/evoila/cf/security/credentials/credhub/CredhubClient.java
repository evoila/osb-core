package de.evoila.cf.security.credentials.credhub;

import de.evoila.cf.broker.bean.CredhubBean;
import de.evoila.cf.broker.model.EnvironmentUtils;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.credential.CertificateCredential;
import de.evoila.cf.broker.model.credential.PasswordCredential;
import de.evoila.cf.broker.model.credential.UsernamePasswordCredential;
import de.evoila.cf.security.credentials.CredentialStore;
import de.evoila.cf.security.utils.CustomClientHttpRequestFactory;
import de.evoila.cf.security.utils.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateParameters;
import org.springframework.credhub.support.certificate.CertificateParametersRequest;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.credhub.support.password.PasswordParameters;
import org.springframework.credhub.support.password.PasswordParametersRequest;
import org.springframework.credhub.support.user.UserCredential;
import org.springframework.credhub.support.user.UserCredentialRequest;
import org.springframework.credhub.support.user.UserParametersRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Rene Schollmeyer, Johannes Hiemer.
 */
@Service
@ConditionalOnBean(CredhubBean.class)
public class CredhubClient implements CredentialStore {

    private static final Logger log = LoggerFactory.getLogger(CredhubClient.class);

    private CredhubBean credhubBean;

    private CredHubOperations credHubOperations;

    private static String SERVICE_BROKER_PREFIX = "sb-";

    public CredhubClient(
            /*
            This is here, to ensure the HttpClient gets created BEFORE any rest calls are being fired, to ensure
            no unnecessary certificate errors occur.
             */
            SimpleClientHttpRequestFactory clientHttpRequestFactory,
            CredhubBean credhubBean, Environment environment, CredHubOperations credHubOperations) {
        this.credhubBean = credhubBean;
        this.credHubOperations = credHubOperations;

        if (EnvironmentUtils.isTestEnvironment(environment)) {
            SERVICE_BROKER_PREFIX += "test-";
        }

        log.trace("Credhub Version is: " + this.credHubOperations.info().version().getVersion());
    }

    private SimpleCredentialName identifier(String instanceId, String valueName) {
        return new SimpleCredentialName(credhubBean.getBoshDirector(),
                SERVICE_BROKER_PREFIX + instanceId, valueName);
    }

    private String buildManifestPlaceHolder(String valueName) {
        return "((" + valueName + "))";
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
        return this.createUser(instanceId, valueName, username, 40);
    }

    @Override
    public UsernamePasswordCredential createUser(ServiceInstance serviceInstance, String valueName, String username, int passwordLength) {
        return this.createUser(serviceInstance.getId(), username, valueName, passwordLength);
    }

    /**
     * @param instanceId
     * @param valueName
     * @param username
     * @param passwordLength
     * @return The createUser method returns the interpolated values of the User and Password Creation. That means it
     * does not return the created Username and Password itself but it returns:
     * * ((valueName.username)) for the username
     * * ((valueName.password)) for the password
     * If you want to get the values itself, please run getUser(serviceInstance, valueName) after you have
     * created the User.
     */
    @Override
    public UsernamePasswordCredential createUser(String instanceId, String valueName, String username, int passwordLength) {
        UserParametersRequest request = UserParametersRequest.builder()
                .name(this.identifier(instanceId, valueName))
                .username(username)
                .parameters(PasswordParameters.builder()
                        .length(passwordLength)
                        .excludeUpper(false)
                        .excludeNumber(false)
                        .excludeLower(false)
                        .includeSpecial(false)
                        .build())
                .build();

        log.info("Creating user credentials for instance with id = " + instanceId);

        CredentialDetails<UserCredential> user = credHubOperations.credentials().generate(request);

        return new UsernamePasswordCredential(buildManifestPlaceHolder(valueName + ".username"),
                buildManifestPlaceHolder(valueName + ".password"));
    }

    @Override
    public UsernamePasswordCredential createUser(ServiceInstance serviceInstance, String valueName, String username, String password) {
        return this.createUser(serviceInstance.getId(), valueName, username, password);
    }

    @Override
    public UsernamePasswordCredential createUser(String instanceId, String valueName, String username, String password) {
        UserCredentialRequest request = UserCredentialRequest.builder()
                .name(this.identifier(instanceId, valueName))
                .value(new UserCredential(username, password))
                .build();

        log.info("Creating user credentials for instance with id = " + instanceId);

        CredentialDetails<UserCredential> user = credHubOperations.credentials().write(request);

        return new UsernamePasswordCredential(buildManifestPlaceHolder(valueName + ".username"),
                buildManifestPlaceHolder(valueName + ".password"));
    }

    @Override
    public UsernamePasswordCredential getUser(ServiceInstance serviceInstance, String valueName) {
        return this.getUser(serviceInstance.getId(), valueName);
    }

    @Override
    public UsernamePasswordCredential getUser(String instanceId, String valueName) {
        CredentialDetails<UserCredential> user = credHubOperations.credentials()
                .getByName(this.identifier(instanceId, valueName), UserCredential.class);

        return new UsernamePasswordCredential(user.getValue().getUsername(), user.getValue().getPassword());
    }

    @Override
    public PasswordCredential createPassword(ServiceInstance serviceInstance, String valueName) {
        return this.createPassword(serviceInstance.getId(), valueName);
    }

    @Override
    public PasswordCredential createPassword(String instanceId, String valueName) {
        return createPassword(instanceId, valueName, 40);
    }

    @Override
    public PasswordCredential createPassword(ServiceInstance serviceInstance, String valueName, int passwordLength) {
        return this.createPassword(serviceInstance.getId(), valueName, passwordLength);
    }

    /**
     * @param instanceId
     * @param valueName
     * @param passwordLength The createPassword method returns the interpolated value of the Password Creation. That means it
     *                       does not return the created Password itself but it returns:
     *                       * ((valueName)) for the password
     *                       If you want to get the value itself, please run getPassword(serviceInstance, valueName) after you have
     *                       created the Password.
     */
    @Override
    public PasswordCredential createPassword(String instanceId, String valueName, int passwordLength) {
        PasswordParametersRequest request = PasswordParametersRequest.builder()
                .name(this.identifier(instanceId, valueName))
                .parameters(PasswordParameters.builder()
                        .length(passwordLength)
                        .excludeUpper(false)
                        .excludeNumber(false)
                        .excludeLower(false)
                        .includeSpecial(false)
                        .build())
                .build();

        log.info("Creating password credentials for instance with id = " + instanceId);

        credHubOperations.credentials().generate(request);

        return new PasswordCredential(buildManifestPlaceHolder(valueName));
    }

    @Override
    public String getPassword(ServiceInstance serviceInstance, String valueName) {
        return this.getPassword(serviceInstance.getId(), valueName);
    }

    @Override
    public String getPassword(String instanceId, String valueName) {
        CredentialDetails<org.springframework.credhub.support.password.PasswordCredential> password = credHubOperations.credentials()
                .getByName(this.identifier(instanceId, valueName), org.springframework.credhub.support.password.PasswordCredential.class);
        return password.getValue().getPassword();
    }

    @Override
    public Map<String, Object> createJson(ServiceInstance serviceInstance, String valueName, Map<String, Object> values) {
        return this.createJson(serviceInstance.getId(), valueName, values);
    }

    /**
     * @param instanceId
     * @param valueName
     * @param values     The createJson method returns the interpolated value of the JSON Creation. That means it
     *                   does not return the created JSON itself but it returns:
     *                   * ((valueName.<value>)) for the JSON, where <value> is the key in the Map<String, Object> of values
     *                   If you want to get the value itself, please run getJson(serviceInstance, valueName) after you have
     *                   created the JSON.
     */
    @Override
    public Map<String, Object> createJson(String instanceId, String valueName, Map<String, Object> values) {
        JsonCredentialRequest request = JsonCredentialRequest.builder()
                .name(this.identifier(instanceId, valueName))
                .value(new JsonCredential(values))
                .build();

        log.info("Creating json credentials for instance with id = " + instanceId);

        CredentialDetails<JsonCredential> json = credHubOperations.credentials().write(request);

        return json.getValue();
    }

    @Override
    public Map<String, Object> getJson(ServiceInstance serviceInstance, String valueName, String key) {
        return this.getJson(serviceInstance.getId(), valueName, key);
    }

    @Override
    public Map<String, Object> getJson(String instanceId, String valueName, String key) {
        CredentialDetails<JsonCredential> json = credHubOperations.credentials()
                .getByName(this.identifier(instanceId, valueName), JsonCredential.class);
        return json.getValue();
    }

    @Override
    public void deleteCredentials(ServiceInstance serviceInstance, String valueName) {
        this.deleteCredentials(serviceInstance.getId(), valueName);
    }

    @Override
    public void deleteCredentials(String instanceId, String valueName) {
        credHubOperations.credentials().deleteByName(this.identifier(instanceId, valueName));
    }

    @Override
    public CertificateCredential createCertificate(ServiceInstance serviceInstance, String valueName, CertificateParameters certificateParameters) {
        return this.createCertificate(serviceInstance.getId(), valueName, certificateParameters);
    }

    @Override
    public CertificateCredential createCertificate(String instanceId, String valueName, CertificateParameters certificateParameters) {
        CertificateParametersRequest request = CertificateParametersRequest.builder()
                .name(this.identifier(instanceId, valueName))
                .parameters(certificateParameters)
                .build();

        log.info("Creating certificate for instance with id = " + instanceId);

        CredentialDetails<CertificateCredential> certificate = credHubOperations.credentials().generate(request);

        return new CertificateCredential(buildManifestPlaceHolder(valueName + ".ca"),
                buildManifestPlaceHolder(valueName + ".certificate"),
                buildManifestPlaceHolder(valueName + ".private_key"));
    }

    @Override
    public void deleteCertificate(ServiceInstance serviceInstance, String valueName) {
        this.deleteCertificate(serviceInstance.getId(), valueName);
    }


    @Override
    public void deleteCertificate(String instanceId, String valueName) {
        deleteCredentials(instanceId, valueName);
    }

    @Override
    public CertificateCredential getCertificate(ServiceInstance serviceInstance, String value) {
        return this.getCertificate(serviceInstance.getId(), value);
    }

    @Override
    public CertificateCredential getCertificate(String instanceId, String valueName) {
        CredentialDetails<CertificateCredential> certificate = credHubOperations.credentials()
                .getByName(this.identifier(instanceId, valueName), CertificateCredential.class);

        return new CertificateCredential(certificate.getValue().getCertificateAuthority(),
                certificate.getValue().getCertificate(),
                certificate.getValue().getPrivateKey());
    }
}
