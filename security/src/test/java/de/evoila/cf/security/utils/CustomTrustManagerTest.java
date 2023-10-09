package de.evoila.cf.security.utils;

import de.evoila.cf.security.keystore.KeyStoreHandler;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomTrustManagerTest {

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
    void getAcceptedIssuersReturnsArray() throws IOException, GeneralSecurityException {
        CustomTrustManager customTrustManager = setUpCustomTrustManager(ca);
        X509Certificate[] result = customTrustManager.getAcceptedIssuers();
        assertTrue(result.length > 0);
    }

    @Test
    void checkServerTrustedDoesNotThrowException() throws IOException, GeneralSecurityException {
        CustomTrustManager customTrustManager = setUpCustomTrustManager(ca);
        X509Certificate[] chain = new X509Certificate[]{(X509Certificate) KeyStoreHandler.loadCertificate(serverCertificate)};
        assertDoesNotThrow(() -> customTrustManager.checkServerTrusted(chain, "ECDHE_RSA"));
    }

    @Test
    void checkServerTrustedDoesThrowException() throws IOException, GeneralSecurityException {
        CustomTrustManager customTrustManager = setUpCustomTrustManager(null);
        X509Certificate[] chain = new X509Certificate[]{(X509Certificate) KeyStoreHandler.loadCertificate(serverCertificate)};
        assertThrows(GeneralSecurityException.class, () -> customTrustManager.checkServerTrusted(chain, "ECDHE_RSA"));
    }


    private final static String serverCertificate = """
            -----BEGIN CERTIFICATE-----
            MIIDPDCCAiSgAwIBAgIUU4SQUjV/4x5d0IZ6wGnAB3wXqG8wDQYJKoZIhvcNAQEL
            BQAwGTEXMBUGA1UEAxMOZG5zLWFwaS10bHMtY2EwHhcNMjIwMzAxMTUxNTAwWhcN
            MjMwMzAxMTUxNTAwWjAXMRUwEwYDVQQDEwxhcGkuYm9zaC1kbnMwggEiMA0GCSqG
            SIb3DQEBAQUAA4IBDwAwggEKAoIBAQDXh1v7y2qlc8q27uODhoujQ3ItderNDVwy
            IPcosdkoUnKJZMeSdN3ER9u5dP1fpnkqTbA/a3ERciYFHlgRkqTdYpd8H/G3iwfE
            9wetz5xVTlVQ/qw9aMUqvFFcyKOH3bWvT8x61xK3h9sLggDNE25hx4ICQruceN55
            stDW+/x9fOrQgyjo0aPj31vCrVrJz/6EvGpTWKGIs5LYIPrLdXzT1MY+YCnHvf4l
            PgqtmIC5LznjDMKN8kX41dcX3/S6fWa7gIoZc5eoxw7riUFLEoqq0Pvqn7MnnCLg
            dnV+3mmaRwFPrihVUHzyRZnzo+czehaKgZUJUY/JCUXXScm8uDztAgMBAAGjfjB8
            MB0GA1UdDgQWBBR9VTG35qYmmDnsrgfHnqdKk3TcajAXBgNVHREEEDAOggxhcGku
            Ym9zaC1kbnMwEwYDVR0lBAwwCgYIKwYBBQUHAwIwHwYDVR0jBBgwFoAUZXJOM4bb
            AA1NFIh0/sJJTjin4mgwDAYDVR0TAQH/BAIwADANBgkqhkiG9w0BAQsFAAOCAQEA
            X6UMvmpYYWwHzJIgoN9glVsRx6CKnAKD2eiFPwuHLICV2D3QYoJvsBGZOz/GRb4V
            JHg3dljfjjZFFDuwd3uSJWSgq3Bi2UdnbvKhbgxYO1PDA1wrbfWP0EQMZBjHQShl
            CjdWiN6cT4vGyhA0DtMjXwWnZyZn9FxJEjhhOtGz8hGuT9LpzUb43yrOmoM6+REY
            NrbwVVUrvY1wUiyothgzkgV1YJMGQwZLURL+FZZjjEc/k5R6Y+Srf2kv3JLUO5xp
            m+GG0jhafEteNcQfPW6q+fqQy6JqxQ1TJFbzpWfLNj49H0kUxjWxJ+3IDb/dAQ/r
            PNFjSgcpN2bCfTToqpZ9dw==
            -----END CERTIFICATE-----\
            """;

    private final static String ca = """
            -----BEGIN CERTIFICATE-----
            MIIDEzCCAfugAwIBAgIURRkZmhg/gJ0TpoxQfbutR9nWg0YwDQYJKoZIhvcNAQEL
            BQAwGTEXMBUGA1UEAxMOZG5zLWFwaS10bHMtY2EwHhcNMjIwMzAxMTUxNDU5WhcN
            MzIwMjI3MTUxNDU5WjAZMRcwFQYDVQQDEw5kbnMtYXBpLXRscy1jYTCCASIwDQYJ
            KoZIhvcNAQEBBQADggEPADCCAQoCggEBANVuMSZT5zlJcOUYBjsi9dsbXVqLi8zj
            CQMba6aNwecu+pqsveyZwjv9ddgbjDmlb0FxzFqaBKPQDbiqWqLY9Y3kHuNupAyC
            cQ3cVbTbFGxeCLeQWFHNQnO2vdgGhFEIRfcKuHgm8iAH3lj27rQq4ffwpENAWPiV
            EpwdYhHOhuVBoUfF40B1c79bI3ELxWTBqlvOjQRWgSJ8mryfSOok6pbCd16WxHFa
            6iyp0E3V89KrfkHmoE0IB3O5sFZOQLzKFnYMoTFtpBoDiiILPKcCMOeaS7oPmaee
            gEAzGffEZPro900Afam9PtQJGysR9YlIByCV5qWrDaUYd/pkRNw0LZkCAwEAAaNT
            MFEwHQYDVR0OBBYEFGVyTjOG2wANTRSIdP7CSU44p+JoMB8GA1UdIwQYMBaAFGVy
            TjOG2wANTRSIdP7CSU44p+JoMA8GA1UdEwEB/wQFMAMBAf8wDQYJKoZIhvcNAQEL
            BQADggEBAJkqTQsJOfdL2UZ34IbHvExqkz5H1yMCGCmqQzCGpl9LtlQGpd3zZVPA
            qDpHiM2j3oQHcDUCQHuNF5V+hxjzv5UNe6K+Mm+WCUQzsLHDNiSHOu+OHwn42kjJ
            JntuvHY5HbrbOvpMTAQL9CvDIlIN7BkdEcm8WTq9rqhlBQx7ExKWjL+PkW1At2N6
            HaFxOjAHjVcmiVYVO+qVPgN6UpY2SHVxBrDGrUr5NOJnyICp5uJjIO+qUzWl7SZY
            BvLCMhL/BzPXLY1rjIzR3cVAU30B99pQcb46iYJN8P9HkhOV+xUf6WZRQQnTYiaK
            UHrVBiuj+dGtooKgrKAS+RNAFWQimhw=
            -----END CERTIFICATE-----\
            """;
}
