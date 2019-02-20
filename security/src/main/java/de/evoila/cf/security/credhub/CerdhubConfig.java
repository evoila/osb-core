package de.evoila.cf.security.credhub;


import de.evoila.cf.broker.bean.CredhubBean;
import de.evoila.cf.broker.model.EnvironmentUtils;
import de.evoila.cf.security.keystore.KeyStoreHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.credhub.autoconfig.CredHubOAuth2AutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubTemplateAutoConfiguration;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.ClientOptions;
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
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;

import javax.naming.ConfigurationException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Map;

/**
 * Created by reneschollmeyer, evoila on 24.10.18.
 */
@Configuration
@ConditionalOnBean(CredhubBean.class)
@AutoConfigureAfter({CredHubOAuth2AutoConfiguration.class, OAuth2ClientAutoConfiguration.class, ReactiveOAuth2ClientAutoConfiguration.class})
@AutoConfigureBefore(CredHubTemplateAutoConfiguration.class)
public class CerdhubConfig {
    @Bean
    public CredHubOperations credHubTemplate(CredHubProperties credHubProperties, ClientOptions clientOptions, @Autowired(required = false) ClientRegistrationRepository clientRegistrationRepository, @Autowired(required = false) OAuth2AuthorizedClientService authorizedClientService, @Autowired CredhubBean credhubBean) throws KeyStoreException, NoSuchAlgorithmException, ConfigurationException, UnrecoverableKeyException, KeyManagementException {
        KeyStoreHandler keyStoreHandler = new KeyStoreHandler();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                new SSLContextBuilder()
                        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                        .loadKeyMaterial(keyStoreHandler.getKeyStore(
                                credhubBean.getCertificate().getCert(),
                                credhubBean.getCertificate().getPrivateKey(),
                                credhubBean.getCertificate().getCa(),
                                credhubBean.getKeystorePassword()), credhubBean.getKeystorePassword().toCharArray())
                        .build(),
                NoopHostnameVerifier.INSTANCE);

        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        ClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);


        if (credHubProperties.getOauth2() != null && credHubProperties.getOauth2().getRegistrationId() != null) {
            if (clientRegistrationRepository != null && authorizedClientService != null) {
                return new CredHubTemplate(credHubProperties, clientHttpRequestFactory, clientRegistrationRepository, authorizedClientService);
            } else {
                throw this.misconfiguredException();
            }
        } else {
            throw this.misconfiguredException();
        }
    }


    private IllegalArgumentException misconfiguredException() {
        return new IllegalArgumentException("A CredHub OAuth2 client registration is configured but Spring Security is not available or the Spring Security OAuth2 client registration is misconfigured");
    }
}

