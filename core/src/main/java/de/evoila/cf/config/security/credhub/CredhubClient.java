package de.evoila.cf.config.security.credhub;

import de.evoila.cf.broker.bean.CredhubBean;
import de.evoila.cf.config.security.AcceptSelfSignedClientHttpRequestFactory;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.OAuth2CredHubTemplate;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.credhub.support.password.PasswordParameters;
import org.springframework.credhub.support.password.PasswordParametersRequest;
import org.springframework.credhub.support.user.UserParametersRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by reneschollmeyer, evoila on 24.10.18.
 */
@Service
public class CredhubClient {

    private CredhubBean credhubBean;

    private CredHubTemplate credHubTemplate;

    private static final String BOSH_DIRECTOR = "bosh-1";

    private static final String SERVICE_BROKER_PREFIX = "sb-";

    public CredhubClient(CredhubBean credhubBean) {
        this.credhubBean = credhubBean;
        ClientHttpRequestFactory clientHttpRequestFactory = new AcceptSelfSignedClientHttpRequestFactory();
        this.credHubTemplate = new OAuth2CredHubTemplate(resource(), credhubBean.getUrl(), clientHttpRequestFactory);
    }

    public OAuth2ProtectedResourceDetails resource() {
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails() {
            @Override
            public boolean isClientOnly() {
                return true;
            }
        };
        resource.setAuthenticationScheme(AuthenticationScheme.form);
        resource.setClientAuthenticationScheme(AuthenticationScheme.header);
        resource.setAccessTokenUri(credhubBean.getOauth2().getAccessTokenUri());
        resource.setClientId(credhubBean.getOauth2().getClientId());
        resource.setClientSecret(credhubBean.getOauth2().getClientSecret());
        return resource;
    }

    public void createUser(String instanceId, String valueName, String username) {
        createUser(instanceId, valueName, username, 40);
    }

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

        credHubTemplate.credentials().generate(request);
    }

    public void createPassword(String instanceId, String valueName) {
        createPassword(instanceId, valueName, 40);
    }

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

        credHubTemplate.credentials().generate(request);
    }

    public void createJson(String intanceId, String valueName, Map<String, Object> values) {
        JsonCredentialRequest request = JsonCredentialRequest.builder()
                .name(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + intanceId, valueName))
                .value(new JsonCredential(values))
                .build();

        credHubTemplate.credentials().write(request);
    }

    public void deleteCredentials(String instanceId, String valueName) {
        credHubTemplate.credentials().deleteByName(new SimpleCredentialName(BOSH_DIRECTOR, SERVICE_BROKER_PREFIX + instanceId, valueName));
    }

    public void createCertificate() {
        
    }

    public void deleteCertificate() {

    }
}
