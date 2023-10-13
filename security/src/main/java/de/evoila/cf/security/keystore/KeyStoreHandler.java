package de.evoila.cf.security.keystore;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.naming.ConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * @author Rene Schollmeyer.
 */
public class KeyStoreHandler {

    public KeyStore getKeyStore(String unformattedClientCertificate, String unformattedPrivateKey, String unformattedCaCertificate,
                                String keyStorePassword) throws ConfigurationException {
        try {
            Certificate clientCertificate = loadCertificate(unformattedClientCertificate);
            PrivateKey privateKey = loadPrivateKey(unformattedPrivateKey);
            Certificate caCertificate = loadCertificate(unformattedCaCertificate);

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca-cert", caCertificate);
            keyStore.setCertificateEntry("client-cert", clientCertificate);
            keyStore.setKeyEntry("client-key", privateKey, keyStorePassword.toCharArray(), new Certificate[]{clientCertificate});
            return keyStore;
        } catch (GeneralSecurityException | IOException e) {
            throw new ConfigurationException(e.getMessage());
        }
    }

    public static Certificate loadCertificate(String certificatePem) throws IOException, GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        final byte[] content = readPemContent(certificatePem);
        return certificateFactory.generateCertificate(new ByteArrayInputStream(content));
    }

    private PrivateKey loadPrivateKey(String privateKeyPem) throws IOException, GeneralSecurityException {
        return pemLoadPrivateKeyPkcs1OrPkcs8Encoded(privateKeyPem);
    }

    private static byte[] readPemContent(String pem) throws IOException {
        final byte[] content;
        try (PemReader pemReader = new PemReader(new StringReader(pem))) {
            final PemObject pemObject = pemReader.readPemObject();
            content = pemObject.getContent();
        }
        return content;
    }

    private static PrivateKey pemLoadPrivateKeyPkcs1OrPkcs8Encoded(String privateKeyPem) throws GeneralSecurityException, IOException {
        final String PEM_PRIVATE_START = "-----BEGIN PRIVATE KEY-----";
        final String PEM_PRIVATE_END = "-----END PRIVATE KEY-----";

        final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
        final String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";

        if (privateKeyPem.contains(PEM_PRIVATE_START)) { // PKCS#8 format
            privateKeyPem = privateKeyPem.replace(PEM_PRIVATE_START, "").replace(PEM_PRIVATE_END, "");
            privateKeyPem = privateKeyPem.replaceAll("\\s", "");

            byte[] pkcs8EncodedKey = Base64.getDecoder().decode(privateKeyPem);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(new PKCS8EncodedKeySpec(pkcs8EncodedKey));
        } else if (privateKeyPem.contains(PEM_RSA_PRIVATE_START)) {  // PKCS#1 format
            privateKeyPem = privateKeyPem.replace(PEM_RSA_PRIVATE_START, "").replace(PEM_RSA_PRIVATE_END, "");
            privateKeyPem = privateKeyPem.replaceAll("\\s", "");

            byte[] pkcs1EncodedKey = Base64.getDecoder().decode(privateKeyPem);

            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pkcs1EncodedKey);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(pkcs8KeySpec);
        }

        throw new GeneralSecurityException("Not supported format of a private key");
    }

}
