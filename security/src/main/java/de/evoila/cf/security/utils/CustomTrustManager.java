package de.evoila.cf.security.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class CustomTrustManager implements X509TrustManager {

    private static Logger log = LoggerFactory.getLogger(CustomTrustManager.class);

    private X509TrustManager defaultTrustManager;
    private X509TrustManager customTrustManager;

    CustomTrustManager(Collection<Certificate> certificate) throws GeneralSecurityException {
        defaultTrustManager = getTrustManager(null);
        KeyStore customKeystore = createKeyStore(certificate);
        customTrustManager = getTrustManager(customKeystore);
    }

    private X509TrustManager getTrustManager(KeyStore keyStore) throws GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        Stream<TrustManager> trustManagerStream = Arrays.stream(trustManagers);

        return trustManagerStream.filter(X509TrustManager.class::isInstance)
                .map(X509TrustManager.class::cast).findFirst().orElseThrow(GeneralSecurityException::new);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        CertificateException defaultCertificateException = null;
        if (defaultTrustManager != null) {
            try {
                defaultTrustManager.checkClientTrusted(chain, authType);
                return;
            } catch (CertificateException e) {
                log.trace("Default trust manager did not know the certificate", e);
                defaultCertificateException = e;
            }
        }

        if (customTrustManager != null) {
            try {
                customTrustManager.checkClientTrusted(chain, authType);
                return;
            } catch (CertificateException e) {
                log.trace("Custom trust manager did not know the certificate", e);
                throw new CertificateException("default and custom truststore trust the Certificate.", e);
            }
        }

        if (defaultCertificateException != null) {
            throw defaultCertificateException;
        }

        throw new CertificateException("Both trust managers are null");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        CertificateException defaultCertificateException = null;
        if (defaultTrustManager != null) {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
                return;
            } catch (CertificateException ex) {
                defaultCertificateException = ex;
            }
        }

        if (customTrustManager != null) {
            try {
                customTrustManager.checkServerTrusted(chain, authType);
                return;
            } catch (CertificateException e) {
                log.trace("Custom trust manager did not know the certificate", e);
                throw new CertificateException("default and custom truststore trust the Certificate.", e);
            }
        }

        if (defaultCertificateException != null) {
            throw defaultCertificateException;
        }

        throw new CertificateException("Both trust managers are null");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        ArrayList<X509Certificate> allIssuers = new ArrayList<>();
        if (defaultTrustManager != null) {
            allIssuers.addAll(Arrays.asList(defaultTrustManager.getAcceptedIssuers()));
        }

        if (customTrustManager != null) {
            allIssuers.addAll(Arrays.asList(customTrustManager.getAcceptedIssuers()));
        }

        return allIssuers.toArray(new X509Certificate[0]);
    }

    private KeyStore createKeyStore(Collection<Certificate> certificates) throws KeyStoreException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keystore.load(null, null);
        } catch (IOException | GeneralSecurityException ex) {
            throw new RuntimeException("Error while loading Keystore", ex);
        }

        for (Certificate certificate : certificates) {
            String alias = certificate.toString();
            keystore.setCertificateEntry(alias, certificate);
        }

        return keystore;
    }
}
