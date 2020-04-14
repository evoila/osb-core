package de.evoila.cf.security.utils;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;

public abstract class CustomClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection) {
            try {
                ((HttpsURLConnection) connection).setSSLSocketFactory(SSLContext.getDefault().getSocketFactory());
            } catch (NoSuchAlgorithmException ex) {
                throw new IOException("Could not set Default SSL context", ex);
            }

            ((HttpsURLConnection) connection).setHostnameVerifier(new NoopHostnameVerifier());
        }
        super.prepareConnection(connection, httpMethod);
    }
}
