package de.evoila.cf.security.credhub;

import de.evoila.cf.broker.bean.CredhubBean;
import de.evoila.cf.security.utils.AcceptSelfSignedClientHttpRequestFactory;
import org.springframework.credhub.core.CredHubClient;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Created by reneschollmeyer, evoila on 15.11.18.
 */
@Service
public class CredhubConnection {

    private static final String GRANT_TYPE="client_credentials";

    private CredhubBean credhubBean;

    public CredhubConnection(CredhubBean credhubBean) {
        this.credhubBean = credhubBean;
    }

    public CredHubTemplate createCredhubTemplate() {

        ClientHttpRequestFactory clientHttpRequestFactory = new AcceptSelfSignedClientHttpRequestFactory();

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource());

        ClientCredentialsAccessTokenProvider clientCredentialsAccessTokenProvider = new ClientCredentialsAccessTokenProvider();
        clientCredentialsAccessTokenProvider.setRequestFactory(clientHttpRequestFactory);

        restTemplate.setAccessTokenProvider(new AccessTokenProviderChain(Arrays.asList(clientCredentialsAccessTokenProvider)));
        CredHubClient.configureRestTemplate(restTemplate, credhubBean.getUrl(), clientHttpRequestFactory);

        return new CredHubTemplate(restTemplate);
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
        resource.setGrantType(GRANT_TYPE);
        return resource;
    }
}

