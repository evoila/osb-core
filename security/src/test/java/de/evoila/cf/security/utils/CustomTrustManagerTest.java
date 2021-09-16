package de.evoila.cf.security.utils;

import de.evoila.cf.security.keystore.KeyStoreHandler;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomTrustManagerTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        try {
            checkCertificateValidity(ca);
        } catch (CertificateExpiredException e) {
            fail("The CA certificate is expired. Please update the certificate in this test class!");
        }

        try {
            checkCertificateValidity(serverCertificate);
        } catch (CertificateExpiredException e) {
            fail("The server certificate is expired. Please update the certificate in this test class!");
        }

        try {
            checkCertificateValidity(intermediateCertificate);
        } catch (CertificateExpiredException e) {
            fail("The intermediate certificate is expired. Please update the certificate in this test class!");
        }
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
        X509Certificate[] chain = new X509Certificate[]{
                (X509Certificate) KeyStoreHandler.loadCertificate(serverCertificate),
                (X509Certificate) KeyStoreHandler.loadCertificate(intermediateCertificate)};
        assertDoesNotThrow(() -> customTrustManager.checkServerTrusted(chain, "ECDHE_RSA"));
    }

    @Test
    public void checkServerTrustedDoesThrowException() throws IOException, GeneralSecurityException {
        CustomTrustManager customTrustManager = setUpCustomTrustManager(null);
        X509Certificate[] chain = new X509Certificate[]{
                (X509Certificate) KeyStoreHandler.loadCertificate(serverCertificate),
                (X509Certificate) KeyStoreHandler.loadCertificate(intermediateCertificate)};
        assertThrows(GeneralSecurityException.class, () -> customTrustManager.checkServerTrusted(chain, "ECDHE_RSA"));
    }

    private CustomTrustManager setUpCustomTrustManager(String ca) throws GeneralSecurityException, IOException {
        Collection<Certificate> certificates;
        if (ca != null) {
            certificates = List.of(KeyStoreHandler.loadCertificate(ca));
        } else {
            certificates = Collections.emptyList();
        }
        return new CustomTrustManager(certificates);
    }

    private static void checkCertificateValidity(String serverCertificate) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream bytes = new ByteArrayInputStream(serverCertificate.getBytes());
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(bytes);
        checkCertificateValidity(certificate);
    }

    private static void checkCertificateValidity(X509Certificate certificate) throws CertificateNotYetValidException, CertificateExpiredException {
        certificate.checkValidity();
    }

    /**
     * This server certificate is valid until September 8, 2031
     */
    private final static String serverCertificate = "-----BEGIN CERTIFICATE-----\n" +
            "    MIIE1jCCAr6gAwIBAgIUehm+VCos+EcFyXHzXkJgci+RWbAwDQYJKoZIhvcNAQEL\n" +
            "    BQAwajELMAkGA1UEBhMCREUxDjAMBgNVBAgMBU1haW56MQ8wDQYDVQQKDAZFdm9p\n" +
            "    bGExHDAaBgNVBAsME09zYlRlc3RJbnRlcm1lZGlhdGUxHDAaBgNVBAMME09zYlRl\n" +
            "    c3RJbnRlcm1lZGlhdGUwHhcNMjEwOTEwMTEzMzUyWhcNMzEwOTA4MTEzMzUyWjCB\n" +
            "    jTElMCMGA1UEAxMcZXZvaWxhIHVuaXQgdGVzdCBjZXJ0aWZpY2F0ZTEXMBUGA1UE\n" +
            "    CxMORGV2ZWxvcG1lbnQgQlUxCzAJBgNVBAYTAkRFMRgwFgYDVQQIEw9SaGVpbmxh\n" +
            "    bmQtUGZhbHoxFDASBgNVBAoTC2V2b2lsYSBHbWJIMQ4wDAYDVQQHEwVNYWluejCC\n" +
            "    ASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAM2rQruvs/C9JEFS+ujS3DPo\n" +
            "    weQFdsHkNvNxoTI+xKPahR58Cvjmb4U0nOq9Ulh77c8jFoFDojzjBR3eCxFaxZo2\n" +
            "    8DECC+KLY1K2OMaKD8qh6xHWcJNnHmt1lYSU6NKOZXBQeoKfz0KIXS+Gt6+xrn0j\n" +
            "    UYQyLaN3FG5iPcfSyUrwrjlp4exdr4dSTX3t/d14YtP0+5h9gdnudKRicQAEHulX\n" +
            "    5xRxmAfybbiaUq7DWGvVCTTPuG6vz6spY/sjldlSqkHUhANAajZPWeN3nE2OfXJa\n" +
            "    OqvDfXaNN3LLCRHcKaYnUbH0qZS/7+5Z0uXL7YBP571a8gddC2mo1x3aQTa4Xy0C\n" +
            "    AwEAAaNQME4wHQYDVR0OBBYEFKmLp41Gqhc7Y+UqYzLqhmDtOC+tMB8GA1UdIwQY\n" +
            "    MBaAFFGFm3+U8tWoeNEIghrPlIhL5j8mMAwGA1UdEwEB/wQCMAAwDQYJKoZIhvcN\n" +
            "    AQELBQADggIBAMczlo+RqoXooxX1PaBAnM4MU2hcpENbu03tJe9p5JD8zOySIrGA\n" +
            "    +Y+jtCoEccqSlW7LnPCLh6VZ5vdlj2Lhi6oX7GNI+b1MNk8wz9d8jgj4cLHiQwJv\n" +
            "    YVXB0yfXnzsKQm4AQ1jUJBLqpNEG003hs58H1t74LYE+aYivniOVcDm+eAvgonk/\n" +
            "    SB3NmqbdHZr+T1LOW0v6OElv6WuAresaXGfGKjEEvpAZrtO2EO9L82HETC6zOXmN\n" +
            "    Pjh1S2tFUKFxisEuFmgWi59+oBWrXw3ge7OCsBW7unFrYsI2EHWsgbTBzdsCIWR2\n" +
            "    t5tU5Q+YS2TDR4hiyGn3FVqjnBD3A1yt4ambeS3XU25TqyrgdWfMJS1F/nSzqZf8\n" +
            "    um2AfYxpwrRhnxOzNg7qoTAVJiMhd8R6lTZdSkZWWEOKnksoAPxrBiZDa4D1OAFn\n" +
            "    OClVWfw+Wy1F0y3nXoHwWTJq6wWOvRsv9A/Qx7nKjUxnDIngg4IHf8SDcV0moV7J\n" +
            "    xomEfTRbhhvFt4u3clV+DoKHpa5qBJ/scVHChNRG6o4E9QlTCxuGHAK2et8BD4+B\n" +
            "    onmA5xCwzPAlsx3hHmE1eMDkp6DQ6lp+VSAn5027ivGLy/VA5B3lEPpCVgCGkVBW\n" +
            "    U9dDr1436A57fTYX1XC0VuDter9ZeqTRVpDe5V6qTuNsPBR0+VGTCfWF\n" +
            "    -----END CERTIFICATE-----";

    /**
     * This intermediate certificate is valid until June 30, 2026
     */
    private final static String intermediateCertificate = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFqjCCA5KgAwIBAgICEAAwDQYJKoZIhvcNAQELBQAwXjELMAkGA1UEBhMCREUx\n" +
            "DjAMBgNVBAgMBU1haW56MQ8wDQYDVQQKDAZFdm9pbGExFjAUBgNVBAsMDU9zYlRl\n" +
            "c3RSb290Q2ExFjAUBgNVBAMMDU9zYlRlc3RSb290Q2EwHhcNMjEwNzAxMTUwNzM5\n" +
            "WhcNMjYwNjMwMTUwNzM5WjBqMQswCQYDVQQGEwJERTEOMAwGA1UECAwFTWFpbnox\n" +
            "DzANBgNVBAoMBkV2b2lsYTEcMBoGA1UECwwTT3NiVGVzdEludGVybWVkaWF0ZTEc\n" +
            "MBoGA1UEAwwTT3NiVGVzdEludGVybWVkaWF0ZTCCAiIwDQYJKoZIhvcNAQEBBQAD\n" +
            "ggIPADCCAgoCggIBANfpkOc6zQlOE6MtBtdZoyMou3zP7QmfThMlCtUA8BVwanqb\n" +
            "GJwD6CpDvwQIyQQz7FfBYOegTsHpsqgMBuMrnAIpFXxnqQITpw05zbUmMoiCHhCw\n" +
            "z81tUDh3c1XQY0hXpxoDlde7aMIzxmMGGL+r8hBsTUTqMNEedLb+UXPM0hDt9W0n\n" +
            "W2j2XHznMSWJeKZaRdwLMHGY/mE4mqZ47r41jpK4+Gu/rBr8ztEc5jIW4XrbovBM\n" +
            "SL5WBX8v+YTyCO0pMjC4c7745oEp57aNXkdPsxAKpYQwuZlyjRAdGIShw3eX2t9r\n" +
            "i4SOFKUi/j2WF5aX662o23rqPNFO2y7LSbWjbxpN5GrISi4LGzlXovR4FqpcKAmC\n" +
            "qywfHrhEZABsniLjX4BPcErQcV61cwPsJ2H/iaPC+0PNP+QRPm7OLY/SaP3LKrOI\n" +
            "+VNaJ/+yPQE5ct1vrMmpIwTSt/g29d5Py1ZwoRXR/SFzYfpd9m5YtijuHlIGqmqE\n" +
            "Har1lEyMNONdSfGhyrY3bd9jzhBBLUxYG3+GYo4qqsYlqgXmgpXfpvCbNmHVFGDQ\n" +
            "irNdXzXuY9Mjk8XrIBQ1t9FV2p7EStigiLdID4p9XnokTKatpsFgZ11wv/iAaADo\n" +
            "3IDQEA+hJDkAJj0Tfy+vuZxA3X/cUwFG31LBOZreBZeuJVuYxdpFudpKoyHBAgMB\n" +
            "AAGjZjBkMB0GA1UdDgQWBBRRhZt/lPLVqHjRCIIaz5SIS+Y/JjAfBgNVHSMEGDAW\n" +
            "gBRiXv2L+N9c1rrPeEmipoTKolD8hzASBgNVHRMBAf8ECDAGAQH/AgEAMA4GA1Ud\n" +
            "DwEB/wQEAwIBhjANBgkqhkiG9w0BAQsFAAOCAgEAcxdxPXTXL/PKSFGADK+09iYE\n" +
            "i1lOJTj8Bascvz5KL3CRbZ5eJYcFuR8jqpyF30F9utiUcBTDfCMCGnNb/Ys7PEki\n" +
            "Fy1NwyWXj30xI/I6NTxSsnygtRe77OuiBSLGrUFuzZ61RWE0MCY407N9kBHz3sxA\n" +
            "ZSOsPRUZ5bI9hdrfHBcYX67N6PXBrYSTO9GdvTfe6ZiDJ6LtMIn1CaSBlIvvxpJY\n" +
            "PLW81LTGLCg0wReGwkD51ttoGnOzWOHUesbIAi9uGxVwCm+KGqFcmvuF6PtPxWHp\n" +
            "r0PLzZ0HLCBDdB8mB+9XQvEPXpwSuwlktNjejGk4oxTrkUJyYsn15Khkq3gYHLoH\n" +
            "rq4483y98Z4ICu6bfJUTi3rlsw9bfzuMthgYkNFFO+aTlcU36RKEFJqssO5yaRBm\n" +
            "HnMdq1L4yGusFQSXeaMNHmcixFqJ3OZwL40OrDpB1XtxyEH5kcggE5e2E0Lpm9A+\n" +
            "lfxf2pv+wdKkMw3ug0ke4Mh24M50SQTlDmi7+T9qiyRSeGwOo529MQsi7qFxuDyh\n" +
            "TqfnK2TzVbTmriorDCUSDEQju+0nGqAtbxolFDbbXLC24UZE64mayEw9nA3T7fhE\n" +
            "paNCIJRQ1zy1f5Ax8ZJa+QHgSUzqlt5aZLlBwghlPhKFvlUuMRgeH1nQGOzQKAAl\n" +
            "zuSw9wzECa66pQxHcMo=\n" +
            "-----END CERTIFICATE-----";

    /**
     * This ca certificate is valid until June 26, 2041
     */
    private final static String ca = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFojCCA4qgAwIBAgIJAIeqWPtJ8KdrMA0GCSqGSIb3DQEBCwUAMF4xCzAJBgNV\n" +
            "BAYTAkRFMQ4wDAYDVQQIDAVNYWluejEPMA0GA1UECgwGRXZvaWxhMRYwFAYDVQQL\n" +
            "DA1Pc2JUZXN0Um9vdENhMRYwFAYDVQQDDA1Pc2JUZXN0Um9vdENhMB4XDTIxMDcw\n" +
            "MTEyNDE1NloXDTQxMDYyNjEyNDE1NlowXjELMAkGA1UEBhMCREUxDjAMBgNVBAgM\n" +
            "BU1haW56MQ8wDQYDVQQKDAZFdm9pbGExFjAUBgNVBAsMDU9zYlRlc3RSb290Q2Ex\n" +
            "FjAUBgNVBAMMDU9zYlRlc3RSb290Q2EwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAw\n" +
            "ggIKAoICAQC7I9pDwfwtV7RmdFU8ZYOXY6xPi1Ejjr6F+z4OIQ2f76GIPKV/61hM\n" +
            "SVBb0TI9rsT1QNCSUa4HK/6zdIRjJ2ndEkFA7tZDR75m6NrK5Xbq7p85dJMyvh+C\n" +
            "ieoY8sSgTO1HLpGUcQdidQ/kRzc0Fz5CNuT6aGbqSCDb46YcOOz6OQYfwx3DpF7U\n" +
            "o4CR4Bcb8WsWdbf9aVRpHhxVPi/EEx7lZZI/xRzH3OIWLTzNPoRda1EH9k8Xa9Yr\n" +
            "v/IdA+wxIsIqQbVuLbiDxKtqH79WTFAzH5n9ikvWx5FU3AV/SgS1lrFedjGWv5uA\n" +
            "R6exb3uoyAeuw3dnH9MkQ9kgWWJyXGvh3It+s0YQyTFAG9aQwTrzY0FAAeAfYvDM\n" +
            "mNW+4n+RPAbKq+Ydo2hVadhT3Sof9ICJM60UzvLfvLMaWtt91W6Nqt8Jte+cLRUa\n" +
            "whSE9Rbqbuqn8dZcxlhUVuQqcM0lkaJ40imsB1Eq3wRxjmRQ+2avYaKIOTY8Qz6B\n" +
            "k5Sl+72BhD9J177oh/res0Cbq1x9FlGyFNDg7ucPRq4dSYbpD/TyceZ/qLtyOyS0\n" +
            "fTxU+U+PI0qliVeZPZ5/gHgYDoob2zh5BAAcBHtMHdbA9bjxyeNYBu/K3h/NKNSe\n" +
            "nq+LongxGP0FbO10b73yYcEG+GhlHslnaKUK9P8Lkrp1Ani5cUq3XQIDAQABo2Mw\n" +
            "YTAdBgNVHQ4EFgQUYl79i/jfXNa6z3hJoqaEyqJQ/IcwHwYDVR0jBBgwFoAUYl79\n" +
            "i/jfXNa6z3hJoqaEyqJQ/IcwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMC\n" +
            "AYYwDQYJKoZIhvcNAQELBQADggIBABTP+UyYJArgFZ7z2yPWPLMWAP3VWVJs0JSV\n" +
            "/A0d4FJQJ1pYE+XQzi+enNv3eMJdzcpKs+xF4R9ZqOkV5h6M+Xcl/CJZoY6B6mqR\n" +
            "sGJP7B0PT915YQDx9uGR5PZcOeU0QMh+Il1ZpZK2uc3rJDsaWbIk5yM8qPUg1hHu\n" +
            "xoalEOO23owiz0sAH/ilic+2EXD8GFpJus/kJfqskAM+TPiNLVIvSW/NuRpi7ra1\n" +
            "al2mfNHeDkz/wK7Ij4AaKr2ApHekGatLx08r8MSN79hNX8xa3FvmczF2lhaNJ4Qt\n" +
            "wrtIATABKMMEOqCAQ0053EMIyCI8qZvcVsfbLCx0dLDz5jm7WvmweNJwJ0YAaR4l\n" +
            "NGfzzgL8xja6RlPWKdty9R6SliEehyWEIGETQl67yH9krhzePqiplxwHPMic9Hxs\n" +
            "XSD8dxBpTI3qh72sLQ0baYL2yNvDJ8s51kNg9Z5eeO2PM1kRyktev0CNbcN8HsO2\n" +
            "hbB5jf8u/BOQpawYXZZBq0bFQBdcvCPR1P9AOEPKettpCrL6bI8Fl2nvKVRfe04g\n" +
            "TU7d85ZKb/LefcDkdzL0OjDF+TA3YOaSsdpzrsHOaTtqhjvBl1UCOvfKO8VsHtit\n" +
            "TcL5YjjSbq4bB8/0+hyQwiEFHSJtVV+l77C+lqEAZCBYdZWyapyRew/vB0Cj57Ax\n" +
            "KfVBvDgm\n" +
            "-----END CERTIFICATE-----";
}
