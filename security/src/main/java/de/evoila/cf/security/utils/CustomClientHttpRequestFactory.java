package de.evoila.cf.security.utils;

import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;

public abstract class CustomClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    //TODO: why overwrite this method?
    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection lConnection) {
            try {
                lConnection.setSSLSocketFactory(SSLContext.getDefault().getSocketFactory());
            } catch (NoSuchAlgorithmException ex) {
                throw new IOException("Could not set Default SSL context", ex);
            }

            lConnection.setHostnameVerifier(new NoopHostnameVerifier());
        }
        super.prepareConnection(connection, httpMethod);
    }
}
