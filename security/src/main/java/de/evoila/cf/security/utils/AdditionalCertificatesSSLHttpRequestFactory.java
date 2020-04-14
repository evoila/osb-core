package de.evoila.cf.security.utils;

import de.evoila.cf.security.keystore.KeyStoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@ConditionalOnProperty(name = "spring.ssl.certificates")
public class AdditionalCertificatesSSLHttpRequestFactory extends CustomClientHttpRequestFactory {

    private static Logger log = LoggerFactory.getLogger(AdditionalCertificatesSSLHttpRequestFactory.class);

    private AdditionalCertificatesSSLHttpRequestFactory(CustomCertificates customCertificates) {
        loadCerts(customCertificates.getCertificates());
    }

    private static void loadCerts(Collection<String> certificates) {
        try {
            List<Certificate> certs = new ArrayList<>(List.of());
            for (String cert : certificates) {
                certs.add(KeyStoreHandler.loadCertificate(cert));
            }

            CustomTrustManager cta = new CustomTrustManager(certs);
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{cta}, new java.security.SecureRandom());
            SSLContext.setDefault(sslContext);
        } catch (GeneralSecurityException | IOException e) {
            log.error("failed to set up customized ssl context with cause ", e);
        }
    }
}
