package de.evoila.cf.security.credhub;


import de.evoila.cf.broker.bean.CredhubBean;
import de.evoila.cf.broker.model.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateParameters;
import org.springframework.credhub.support.certificate.CertificateParametersRequest;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.credhub.support.password.PasswordCredential;
import org.springframework.credhub.support.password.PasswordParameters;
import org.springframework.credhub.support.password.PasswordParametersRequest;
import org.springframework.credhub.support.user.UserCredential;
import org.springframework.credhub.support.user.UserParametersRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by reneschollmeyer, evoila on 24.10.18.
 */
@Service
@ConditionalOnBean(CredhubBean.class)
public class CredhubClient {

    private static final Logger log = LoggerFactory.getLogger(CredhubClient.class);

    private CredhubBean credhubBean;

    private CredHubTemplate credHubTemplate;

    private Environment environment;

    private CredhubConnection credhubConnection;

    private static final String BOSH_DIRECTOR = "bosh-1";

    private static String SERVICE_BROKER_PREFIX = "sb-";

    public CredhubClient(CredhubBean credhubBean, Environment environment, CredhubConnection credhubConnection) {
        this.credhubBean = credhubBean;
        this.environment = environment;

        if(EnvironmentUtils.isTestEnvironment(environment)) {
            SERVICE_BROKER_PREFIX += "test-";
        }

        this.credHubTemplate = credhubConnection.createCredhubTemplate();

        if(this.credHubTemplate != null) {
            log.info("Successfully establihsed a connection to Credhub.");
        }
    }

    public void createUser(String instanceId, String valueName, String username) {
        createUser(instanceId, valueName, username, 40);
    }

    /**
     * The deployment manifest can access the user credentials via ((valueName.username)) and ((valueName.password))
     */
    public void createUser(String instanceId, String valueName, String username, int passwordLength) {
        UserParametersRequest request = UserParametersRequest.builder()
                .name(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName))
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

        credHubTemplate.credentials().generate(request);
    }

    public String getUserName(String instanceId, String valueName) {
        CredentialDetails<UserCredential> user = credHubTemplate.credentials().getByName(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName), UserCredential.class);
        return user.getValue().getUsername();
    }

    public String getUserPassword(String instanceId, String valueName) {
        CredentialDetails<UserCredential> user = credHubTemplate.credentials().getByName(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName), UserCredential.class);
        return user.getValue().getPassword();
    }

    public void createPassword(String instanceId, String valueName) {
        createPassword(instanceId, valueName, 40);
    }

    /**
     * The deployment manifest can access the password credentials via ((valueName))
     */
    public void createPassword(String instanceId, String valueName, int passwordLength) {
        PasswordParametersRequest request = PasswordParametersRequest.builder()
                .name(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName))
                .parameters(PasswordParameters.builder()
                        .length(passwordLength)
                        .excludeUpper(false)
                        .excludeNumber(false)
                        .excludeLower(false)
                        .includeSpecial(false)
                        .build())
                .build();

        log.info("Creating password credentials for instance with id = " + instanceId);

        credHubTemplate.credentials().generate(request);
    }

    public String getPassword(String instanceId, String valueName) {
        CredentialDetails<PasswordCredential> password = credHubTemplate.credentials().getByName(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName), PasswordCredential.class);
        return password.getValue().getPassword();
    }

    /**
     * The deployment manifest can access the every value in the json credentials via ((valueName.<value>))
     */
    public void createJson(String instanceId, String valueName, Map<String, Object> values) {
        JsonCredentialRequest request = JsonCredentialRequest.builder()
                .name(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName))
                .value(new JsonCredential(values))
                .build();

        log.info("Creating json credentials for instance with id = " + instanceId);

        credHubTemplate.credentials().write(request);
    }

    public Object getJson(String instanceId, String valueName, String key) {
        CredentialDetails<JsonCredential> json = credHubTemplate.credentials().getByName(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName), JsonCredential.class);
        return json.getValue().get(key);
    }

    public void deleteCredentials(String instanceId, String valueName) {
        credHubTemplate.credentials().deleteByName(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName));
    }

    public void createCertificate(String instanceId, String valueName, CertificateParameters certificateParameters) {
        CertificateParametersRequest request = CertificateParametersRequest.builder()
                .name(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName))
                .parameters(certificateParameters)
                .build();

        log.info("Creating certificate for instance with id = " + instanceId);

        credHubTemplate.credentials().generate(request);
    }

    public void deleteCertificate(String instanceId, String valueName) {
        deleteCredentials(instanceId, valueName);
    }

    public String getCertificate(String instanceId, String valueName) {
        CredentialDetails<CertificateCredential> certificate = credHubTemplate.credentials().getByName(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName), CertificateCredential.class);
        return certificate.getValue().getCertificate();
    }

    public String getCertificateAuthority(String instanceId, String valueName) {
        CredentialDetails<CertificateCredential> certificate = credHubTemplate.credentials().getByName(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName), CertificateCredential.class);
        return certificate.getValue().getCertificateAuthority();
    }

    public String getPrivateKey(String instanceId, String valueName) {
        CredentialDetails<CertificateCredential> certificate = credHubTemplate.credentials().getByName(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName), CertificateCredential.class);
        return certificate.getValue().getPrivateKey();
    }
}
