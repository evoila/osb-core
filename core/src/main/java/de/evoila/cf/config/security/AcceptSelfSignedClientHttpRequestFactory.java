package de.evoila.cf.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
@ConditionalOnProperty(name = "spring.ssl.acceptselfsigned", havingValue = "true", matchIfMissing = false)
public class AcceptSelfSignedClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

	private final HostnameVerifier verifier;

	public AcceptSelfSignedClientHttpRequestFactory() {
		this.verifier = new NullHostnameVerifier();
		trustSelfSignedSSL();
	}

	public AcceptSelfSignedClientHttpRequestFactory(HostnameVerifier verifier) {
		this.verifier = verifier;
		trustSelfSignedSSL();
	}

	public static void trustSelfSignedSSL() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				@Override
				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLContext.setDefault(ctx);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public class NullHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	@Override
	protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
		if (connection instanceof HttpsURLConnection) {
			((HttpsURLConnection) connection).setHostnameVerifier(verifier);
		}
		super.prepareConnection(connection, httpMethod);
	}

}