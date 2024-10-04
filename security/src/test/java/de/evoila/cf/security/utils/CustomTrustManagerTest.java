package de.evoila.cf.security.utils;

import de.evoila.cf.security.keystore.KeyStoreHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.cert.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomTrustManagerTest {

    @BeforeAll
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
        X509Certificate[] chain = new X509Certificate[]{
                (X509Certificate) KeyStoreHandler.loadCertificate(serverCertificate)};
        assertDoesNotThrow(() -> customTrustManager.checkServerTrusted(chain, "ECDHE_RSA"));
    }

    @Test
    void checkServerTrustedDoesThrowException() throws IOException, GeneralSecurityException {
        CustomTrustManager customTrustManager = setUpCustomTrustManager(null);
        X509Certificate[] chain = new X509Certificate[]{
                (X509Certificate) KeyStoreHandler.loadCertificate(serverCertificate)};
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

    private final static String serverCertificate = """
            -----BEGIN CERTIFICATE-----
            MIIDjDCCAnSgAwIBAgIUNcfD62yRYK1bkbmhHRijSIJlmo0wDQYJKoZIhvcNAQEL
            BQAwRTELMAkGA1UEBhMCQVUxEzARBgNVBAgMClNvbWUtU3RhdGUxITAfBgNVBAoM
            GEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDAeFw0yNDA5MzAwODQwMDBaFw0yNzAx
            MDMwODQwMDBaMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEw
            HwYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwggEiMA0GCSqGSIb3DQEB
            AQUAA4IBDwAwggEKAoIBAQC/bcNKzikg6XE1JqFdlNU0Ar2dEHzzEJbQQtzeqS7k
            TvYD5BSSJMTdEsTgNlDOQZWq054Eu9nMvk6AT7uilws7y5SC0UrRnQ4cy3Pis16u
            TeVHV3p0Q/FnBcvDTQaK6u01ONLx5JivpFlm4KOSmBalaJeBJjnEbDN8yZBAYAyU
            6q6A5TwmhNrAoG8rEe+flYB5p8gNjMvcTjl+Y+KhPhAtEo3KimK7tXplIXU2ROZE
            K5iecghDVlsf6X1Itj0BtbBfCtwdfcTwwptXf5qMzJRPz3Xwr54GGEbXEh+gi6Ab
            EGWPQ0w7IR5fWrpVexCxGqJp1ws6b7bzMXOx8TWKe4QVAgMBAAGjdDByMB8GA1Ud
            IwQYMBaAFOsl+8dDYDa9hEb8+wyxAephtT96MAkGA1UdEwQCMAAwCwYDVR0PBAQD
            AgTwMBgGA1UdEQQRMA+CDWhlbGxmaXNoLnRlc3QwHQYDVR0OBBYEFNSo4FSxhfqH
            Y/SIesJXtI1MokbLMA0GCSqGSIb3DQEBCwUAA4IBAQCXdsAPEi9n8zrN6HB9NdPT
            Ztmbc07gtAquOezPoUdJq+2i6Ce73Y4sIdcfgdYCuA0SMwTtdFajTKT+cN8jjcy7
            YI3ZXiNu3WxAUJXeUIYKL7PjKP0nHFaN/W2qxxvel47gX1FMD/7/Bm1wX4ePEmP+
            P3o/iy4POupxgwCzitGSKg6cQcRjwio6J6JFXTq7qEF4cTVZDL7/UmbOHtz3mb1o
            +T132P6OLBIXHB606Imu29du8GUCg2MnxDqJRSlc8dLMWucwhEEO9SMOqVeTLu89
            O7huRbxuvKxg0K0jaSbXttOb5bbc4J1bhBiq4NXtqIwRNEKrVH6nnZz73dK2rUfK
            -----END CERTIFICATE-----\
            """;

    private final static String ca = """
            -----BEGIN CERTIFICATE-----
            MIIDazCCAlOgAwIBAgIUJXUpHRBcyDn2RCelAgyZdITohPgwDQYJKoZIhvcNAQEL
            BQAwRTELMAkGA1UEBhMCQVUxEzARBgNVBAgMClNvbWUtU3RhdGUxITAfBgNVBAoM
            GEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDAeFw0yNDA5MzAwODM2MzVaFw0yOTA5
            MjkwODM2MzVaMEUxCzAJBgNVBAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEw
            HwYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQwggEiMA0GCSqGSIb3DQEB
            AQUAA4IBDwAwggEKAoIBAQDcjgj/SgI0FlG+xnuwV8ghkbo4SfG1iWJE5fpAcWPC
            E8dkBQI6rGPb40lfYboyPYykiTPRdZlhKfyIzeHPgvspS1G/qIdM3YEc8LrVl75g
            g1raOFFf92aklv4wlhoUBlD5HrrZkVfeEUIAqgswki3DnRPFfTQf2jeOrafkvx3I
            6UYxWvwpN6WH95HTFQ2TD3/FeLTZ78Mp0trJVb1+ZXg0eDbYkhh4P2qwDpqaHYr1
            wJTMziplny1UVEdRJCKnkBP7o4i1SVqNbunR5iigbkJraE5FqdmNhw8CjvQZesbB
            9VN56yg5Mon2IvqvRq8pHYA2L+kNbgJORUEL3Rki6UHZAgMBAAGjUzBRMB0GA1Ud
            DgQWBBTrJfvHQ2A2vYRG/PsMsQHqYbU/ejAfBgNVHSMEGDAWgBTrJfvHQ2A2vYRG
            /PsMsQHqYbU/ejAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQDB
            1gSx/+CPyvW7hDN4ZXzzljGSRa4+xiJPfk8F41Jrf17617Aq7q8i7W7VkwajQPoC
            5LfrIW8WHg9sEBEAh81nitkfmvATilNZXoCMbpm8WrS3Erq6c+vKWUBqFVp5QgCe
            CM7JIAkhQrPFAPp1Jihy6J6mfOEUJFSeCujASUXfnUnq6mYKHnP98V4gz1Abow61
            rFYTtZzdT/dJjPihfbaJuPXtX57ENoPmdbdrUnqzznQBeIJWV05tmFwXIX7itFsb
            /OtAZsFThGFpa1fpkVOy2jDsfusBkHUZzZVJqzUFt0bHY9gL7yu2vIoh3jL/HkPy
            ZbXPlQWqjnVBxv3op+Nc
            -----END CERTIFICATE-----\
            """;

}
