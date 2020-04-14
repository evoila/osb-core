package de.evoila.cf.security.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@Service
@ConditionalOnProperty(name = "spring.ssl.acceptselfsigned", havingValue = "true")
public class AcceptSelfSignedClientHttpRequestFactory extends CustomClientHttpRequestFactory {

    public AcceptSelfSignedClientHttpRequestFactory() {
        trustSelfSignedSSL();
    }

    private static void trustSelfSignedSSL() {
        try {
            TrustManager[] trustAllCerts = new X509TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] xcs, String string) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] xcs, String string) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }

            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLContext.setDefault(sslContext);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
