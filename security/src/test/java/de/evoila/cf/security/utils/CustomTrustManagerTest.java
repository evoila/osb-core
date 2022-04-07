package de.evoila.cf.security.utils;

import de.evoila.cf.security.keystore.KeyStoreHandler;


import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomTrustManagerTest {

    private CustomTrustManager setUpCustomTrustManager(String ca) throws GeneralSecurityException, IOException {
        Collection<Certificate> certificates;
        if (ca != null) {
            certificates = List.of(KeyStoreHandler.loadCertificate(ca));
        } else {
            certificates = Collections.emptyList();
        }
        return new CustomTrustManager(certificates);
    }

    @Test
    public void getAcceptedIssuersReturnsArray() throws IOException, GeneralSecurityException {
        CustomTrustManager customTrustManager = setUpCustomTrustManager(ca);
        X509Certificate[] result = customTrustManager.getAcceptedIssuers();
        assertTrue(result.length > 0);
    }
    
    @Test
    public void checkServerTrustedDoesNotThrowException() throws IOException, GeneralSecurityException {
        CustomTrustManager customTrustManager = setUpCustomTrustManager(ca);
        X509Certificate[] chain = new X509Certificate[]{(X509Certificate) KeyStoreHandler.loadCertificate(serverCertificate)};
        assertDoesNotThrow(() -> customTrustManager.checkServerTrusted(chain, "ECDHE_RSA"));
    }

    @Test
    public void checkServerTrustedDoesThrowException() throws IOException, GeneralSecurityException {
        CustomTrustManager customTrustManager = setUpCustomTrustManager(null);
        X509Certificate[] chain = new X509Certificate[]{(X509Certificate) KeyStoreHandler.loadCertificate(serverCertificate)};
        assertThrows(GeneralSecurityException.class, () -> customTrustManager.checkServerTrusted(chain, "ECDHE_RSA"));
    }


    private final static String serverCertificate = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDPDCCAiSgAwIBAgIUU4SQUjV/4x5d0IZ6wGnAB3wXqG8wDQYJKoZIhvcNAQEL\n" +
            "BQAwGTEXMBUGA1UEAxMOZG5zLWFwaS10bHMtY2EwHhcNMjIwMzAxMTUxNTAwWhcN\n" +
            "MjMwMzAxMTUxNTAwWjAXMRUwEwYDVQQDEwxhcGkuYm9zaC1kbnMwggEiMA0GCSqG\n" +
            "SIb3DQEBAQUAA4IBDwAwggEKAoIBAQDXh1v7y2qlc8q27uODhoujQ3ItderNDVwy\n" +
            "IPcosdkoUnKJZMeSdN3ER9u5dP1fpnkqTbA/a3ERciYFHlgRkqTdYpd8H/G3iwfE\n" +
            "9wetz5xVTlVQ/qw9aMUqvFFcyKOH3bWvT8x61xK3h9sLggDNE25hx4ICQruceN55\n" +
            "stDW+/x9fOrQgyjo0aPj31vCrVrJz/6EvGpTWKGIs5LYIPrLdXzT1MY+YCnHvf4l\n" +
            "PgqtmIC5LznjDMKN8kX41dcX3/S6fWa7gIoZc5eoxw7riUFLEoqq0Pvqn7MnnCLg\n" +
            "dnV+3mmaRwFPrihVUHzyRZnzo+czehaKgZUJUY/JCUXXScm8uDztAgMBAAGjfjB8\n" +
            "MB0GA1UdDgQWBBR9VTG35qYmmDnsrgfHnqdKk3TcajAXBgNVHREEEDAOggxhcGku\n" +
            "Ym9zaC1kbnMwEwYDVR0lBAwwCgYIKwYBBQUHAwIwHwYDVR0jBBgwFoAUZXJOM4bb\n" +
            "AA1NFIh0/sJJTjin4mgwDAYDVR0TAQH/BAIwADANBgkqhkiG9w0BAQsFAAOCAQEA\n" +
            "X6UMvmpYYWwHzJIgoN9glVsRx6CKnAKD2eiFPwuHLICV2D3QYoJvsBGZOz/GRb4V\n" +
            "JHg3dljfjjZFFDuwd3uSJWSgq3Bi2UdnbvKhbgxYO1PDA1wrbfWP0EQMZBjHQShl\n" +
            "CjdWiN6cT4vGyhA0DtMjXwWnZyZn9FxJEjhhOtGz8hGuT9LpzUb43yrOmoM6+REY\n" +
            "NrbwVVUrvY1wUiyothgzkgV1YJMGQwZLURL+FZZjjEc/k5R6Y+Srf2kv3JLUO5xp\n" +
            "m+GG0jhafEteNcQfPW6q+fqQy6JqxQ1TJFbzpWfLNj49H0kUxjWxJ+3IDb/dAQ/r\n" +
            "PNFjSgcpN2bCfTToqpZ9dw==\n" +
            "-----END CERTIFICATE-----";

    private final static String ca = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDEzCCAfugAwIBAgIURRkZmhg/gJ0TpoxQfbutR9nWg0YwDQYJKoZIhvcNAQEL\n" +
            "BQAwGTEXMBUGA1UEAxMOZG5zLWFwaS10bHMtY2EwHhcNMjIwMzAxMTUxNDU5WhcN\n" +
            "MzIwMjI3MTUxNDU5WjAZMRcwFQYDVQQDEw5kbnMtYXBpLXRscy1jYTCCASIwDQYJ\n" +
            "KoZIhvcNAQEBBQADggEPADCCAQoCggEBANVuMSZT5zlJcOUYBjsi9dsbXVqLi8zj\n" +
            "CQMba6aNwecu+pqsveyZwjv9ddgbjDmlb0FxzFqaBKPQDbiqWqLY9Y3kHuNupAyC\n" +
            "cQ3cVbTbFGxeCLeQWFHNQnO2vdgGhFEIRfcKuHgm8iAH3lj27rQq4ffwpENAWPiV\n" +
            "EpwdYhHOhuVBoUfF40B1c79bI3ELxWTBqlvOjQRWgSJ8mryfSOok6pbCd16WxHFa\n" +
            "6iyp0E3V89KrfkHmoE0IB3O5sFZOQLzKFnYMoTFtpBoDiiILPKcCMOeaS7oPmaee\n" +
            "gEAzGffEZPro900Afam9PtQJGysR9YlIByCV5qWrDaUYd/pkRNw0LZkCAwEAAaNT\n" +
            "MFEwHQYDVR0OBBYEFGVyTjOG2wANTRSIdP7CSU44p+JoMB8GA1UdIwQYMBaAFGVy\n" +
            "TjOG2wANTRSIdP7CSU44p+JoMA8GA1UdEwEB/wQFMAMBAf8wDQYJKoZIhvcNAQEL\n" +
            "BQADggEBAJkqTQsJOfdL2UZ34IbHvExqkz5H1yMCGCmqQzCGpl9LtlQGpd3zZVPA\n" +
            "qDpHiM2j3oQHcDUCQHuNF5V+hxjzv5UNe6K+Mm+WCUQzsLHDNiSHOu+OHwn42kjJ\n" +
            "JntuvHY5HbrbOvpMTAQL9CvDIlIN7BkdEcm8WTq9rqhlBQx7ExKWjL+PkW1At2N6\n" +
            "HaFxOjAHjVcmiVYVO+qVPgN6UpY2SHVxBrDGrUr5NOJnyICp5uJjIO+qUzWl7SZY\n" +
            "BvLCMhL/BzPXLY1rjIzR3cVAU30B99pQcb46iYJN8P9HkhOV+xUf6WZRQQnTYiaK\n" +
            "UHrVBiuj+dGtooKgrKAS+RNAFWQimhw=\n" +
            "-----END CERTIFICATE-----";
}
