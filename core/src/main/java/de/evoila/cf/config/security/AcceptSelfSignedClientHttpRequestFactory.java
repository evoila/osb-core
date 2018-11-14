package de.evoila.cf.config.security;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Service
@ConditionalOnProperty(name = "spring.ssl.acceptselfsigned", havingValue = "true", matchIfMissing = false)
public class AcceptSelfSignedClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

	private final HostnameVerifier noopHostnameVerifier;

	public AcceptSelfSignedClientHttpRequestFactory() {
		this.noopHostnameVerifier = new NoopHostnameVerifier();
		trustSelfSignedSSL();
	}

	public AcceptSelfSignedClientHttpRequestFactory(HostnameVerifier verifier) {
		this.noopHostnameVerifier = verifier;
		trustSelfSignedSSL();
	}

	public static void trustSelfSignedSSL() {
		try {
            TrustManager[] trustAllCerts = new X509TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] xcs, String string) {}

                    @Override
                    public void checkServerTrusted(X509Certificate[] xcs, String string) {}

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }

			};
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			SSLContext.setDefault(sslContext);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
		if (connection instanceof HttpsURLConnection) {
		    try {
                ((HttpsURLConnection) connection).setSSLSocketFactory(SSLContext.getDefault().getSocketFactory());
            } catch (NoSuchAlgorithmException ex) {
		        throw new IOException("Could not set Default SSL context", ex);
            }

			((HttpsURLConnection) connection).setHostnameVerifier(noopHostnameVerifier);
		}
		super.prepareConnection(connection, httpMethod);
	}

}