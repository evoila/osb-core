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
            "MIIEgzCCAuugAwIBAgIRAKizEXy4dr0Nc8NOJGTkYiUwDQYJKoZIhvcNAQELBQAw\n" +
            "MzEMMAoGA1UEBhMDVVNBMRYwFAYDVQQKEw1DbG91ZCBGb3VuZHJ5MQswCQYDVQQD\n" +
            "EwJjYTAeFw0yMDAzMjYxMDUwMjVaFw0yMTAzMjYxMDUwMjVaMD0xDDAKBgNVBAYT\n" +
            "A1VTQTEWMBQGA1UEChMNQ2xvdWQgRm91bmRyeTEVMBMGA1UEAxMMMTkyLjE2OC41\n" +
            "MC42MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAqqncfQufZeLWQfeA\n" +
            "5pN8FiAPotruIaiUJhQKg5RYr+fybMOBL7M8LU75zBTUeXxiBE9gHsl8vchiW/N4\n" +
            "QiOqUmoIRiTex7UAVYKe9D3vs8ja32Muj6vnqhoElFaPXjMydMthC1U9vWOU25vK\n" +
            "DpQqBLpOWIR6ep80n6A/a+BU/hOYc3xMLpRl5a4C8LJUaHqF9jnyvWxpjWF3y0j0\n" +
            "tugQVquZtuvmMIEQVOOsECLgAJzGv5QTHTGeb3KOWmv5xObAB85q7vMwSrpME9zc\n" +
            "5dS3SbKDVPP5fhLm+Gfk7liflqwUjHl9o2enZUJga3BAm4eMAI6BuKkwRxkf24gd\n" +
            "R45G5uB4MNdkf1WxM3eNTEKrUSBlJu+lbCCDjQZ6AQLUDQ5rCLe/zV6XVWtsZN8W\n" +
            "XMu3V0J4h+qsnhHFxlXqoNgleJjsHeBmVdb9GnOwrWowdPNtoBhgB4xqID+28fZD\n" +
            "HAbOXrvvUgZajuVTIf499HNJpjU/nYJxrmJ4gSrNKEpikHBHAgMBAAGjgYcwgYQw\n" +
            "DgYDVR0PAQH/BAQDAgWgMBMGA1UdJQQMMAoGCCsGAQUFBwMBMAwGA1UdEwEB/wQC\n" +
            "MAAwHQYDVR0OBBYEFNZnb9lnvEs2ipu5fOPqiK6/flm+MB8GA1UdIwQYMBaAFG0k\n" +
            "CXWPm1RqUdD58uBYRtYFZo3/MA8GA1UdEQQIMAaHBMCoMgYwDQYJKoZIhvcNAQEL\n" +
            "BQADggGBADfOVYPUe5tLNmHV9OytqYBEG5G1ym9Ra1epiwHN9IyzXcnSv4alA9Xz\n" +
            "mrq1A855+OJtRyj7jvWWzH3RrXVMSeTW1pxfiDiBVsYxZhmhcLh4lW/kxuuiqON9\n" +
            "IP3MTiLRGiX/D+HgVBSFkxSztpAbKe7FyghlEd+P0GbbMug2PYd/DZz4J32KvJws\n" +
            "vuAjDNDDjjBXEKzUP7+HdSi4WHaATnjkBD+r57PTluorMdwS53XGjh3HFtAK241T\n" +
            "Er9V4GajUFhGTtqpi8t1vpQEifrY2brjTo+nQVXN0Qv0NybnHW6wltpRlhYHSd4f\n" +
            "rwYvRECTmvLstHq46xow8IRHzwDu7H1pXeRsRfV2xv/JO951H7puUrcPH6B0f/h2\n" +
            "2x5Z4ox0kq4BYQicpD+5zCUozdyzlQo6IzQTUiTrzIHFHRfu7DLRlOaxTYZRamfz\n" +
            "MmPqYH0diDymO/W0fq7NGuSwjReqbU0wNB844x21Luvb+73aypBMLgmUUcivDuoa\n" +
            "ltOmNKYY/A==\n" +
            "-----END CERTIFICATE-----\n";

    private final static String ca = "-----BEGIN CERTIFICATE-----\n" +
            "MIIEVDCCArygAwIBAgIRANqHr0KWEc0TF+wzqsC7CCMwDQYJKoZIhvcNAQELBQAw\n" +
            "MzEMMAoGA1UEBhMDVVNBMRYwFAYDVQQKEw1DbG91ZCBGb3VuZHJ5MQswCQYDVQQD\n" +
            "EwJjYTAeFw0yMDAzMjYxMDUwMTZaFw0yMTAzMjYxMDUwMTZaMDMxDDAKBgNVBAYT\n" +
            "A1VTQTEWMBQGA1UEChMNQ2xvdWQgRm91bmRyeTELMAkGA1UEAxMCY2EwggGiMA0G\n" +
            "CSqGSIb3DQEBAQUAA4IBjwAwggGKAoIBgQC1nld6HCvhn23DNup74ruEy80+SGxr\n" +
            "xnt2gK882kza/KCEYZ377CJy4M5dd/uCLPDFwDUVp/ViBbXNoln2iRrLVEVxQXpt\n" +
            "Rs6cB/wOkxmsOGXlB67pHvw9Imz3MXhjT26akhdcAE1Gb4a5SSWIfYrzEzGvrp2A\n" +
            "CrU7Mz3s3l7ogGq5d+nKHj/8oSmaBx0oIITcoFdBBprdEbjvG649iRjRjVDWSBDx\n" +
            "uYQens+jpdIdfuDT29s0zC4w86sryYlpRWHxsItdjxycMAF2bFDxTbym5c72YIQY\n" +
            "+paa2qIHgQZlAStJ9R1NRvlmV7MlDFo2gZHUsxbrBYg63nFAT5ybELnOKxctx9Kl\n" +
            "YD9piuzt8N1FqxogRgBBq6/MFLbA8MZ7wfolWPOlVxpooYQzlgv8vW5CFpaqL4k+\n" +
            "C1Lghby6Douwu2ubfVAWJ6L0xUWmNaW4WP/dUQM/lSdFWchnwArrf3WXoOi1VUnN\n" +
            "MumOUyV6XdwpdxAhK94EK7rmIBRIOd2CNt8CAwEAAaNjMGEwDgYDVR0PAQH/BAQD\n" +
            "AgEGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFG0kCXWPm1RqUdD58uBYRtYF\n" +
            "Zo3/MB8GA1UdIwQYMBaAFG0kCXWPm1RqUdD58uBYRtYFZo3/MA0GCSqGSIb3DQEB\n" +
            "CwUAA4IBgQBN8Qc+in9xwVwZaQkDKr62lPyOxQMIR/ALjvIq5VZqUA+3rGpIAOVI\n" +
            "9uYtXrjiqCXeHOWEFHh6bwi5gRmucM85a5ymQmlINRpLRBYrdeX2myqLKSJdvKvb\n" +
            "fh/3kMloubvpbbmp8I0CqchUo37y6FP4yU6nOlNU0CJG/qpF4pNIeKHXGfdvmoU8\n" +
            "eLcUJeOUYPaLBnmAglQS+BwsCRaniEe2hs2cUIZMTO4o1g7a2EfTHUy2oQfLlXPF\n" +
            "wd1NVfc01lOzR56ZEv1GbtFdUUkjbo0IBjxOpUcH9cn3vvkxzvxVam5UoMcai/Vt\n" +
            "T7FC7Dvt6mqRxrBg9xx9Zx3oM1HAQBuyHvzDyPqfbQH/05cyLkQgwLUvtpTR7WSf\n" +
            "mLKOfbIwMhWloXzcYF5L7KpCEsraSB5gx90IylX2PjwRHUirnqVAgxddcGCAh8cf\n" +
            "dxP8AYRAGDETrBzzVkL713rDP5uUBKs/ircQ4tLnZBNQMy/xHMsNCtO/S31Kz6YQ\n" +
            "bHNli8qPpO0=\n" +
            "-----END CERTIFICATE-----";
}
