package de.evoila.cf.security.credhub;

import de.evoila.cf.broker.bean.CredhubBean;
import de.evoila.cf.security.keystore.KeyStoreHandler;
import de.evoila.cf.security.utils.AcceptSelfSignedClientHttpRequestFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.credhub.core.CredHubClient;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.stereotype.Service;

import javax.naming.ConfigurationException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;

/**
 * Created by reneschollmeyer, evoila on 15.11.18.
 */
@Service
public class CredhubConnection {

    private static final String GRANT_TYPE="client_credentials";

    private CredhubBean credhubBean;

    private KeyStoreHandler keyStoreHandler;

    //private ClientHttpRequestFactory clientHttpRequestFactory;

    public CredhubConnection(CredhubBean credhubBean, KeyStoreHandler keyStoreHandler) {
        this.credhubBean = credhubBean;
        this.keyStoreHandler = keyStoreHandler;
    }

    /*@ConditionalOnBean(AcceptSelfSignedClientHttpRequestFactory.class)
    @Autowired(required = false)
    private void selfSignedRestTemplate(AcceptSelfSignedClientHttpRequestFactory requestFactory) {
        clientHttpRequestFactory = requestFactory;
    }*/

    public CredHubTemplate createCredhubTemplate() throws KeyStoreException, NoSuchAlgorithmException, ConfigurationException, UnrecoverableKeyException, KeyManagementException {

        /*if (clientHttpRequestFactory == null) {
            clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        }*/

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .loadKeyMaterial(keyStoreHandler.getKeyStore(), KeyStoreHandler.TEMPORARY_KEY_PASSWORD.toCharArray())
                    .build(),
                NoopHostnameVerifier.INSTANCE);

        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();

        ClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

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

