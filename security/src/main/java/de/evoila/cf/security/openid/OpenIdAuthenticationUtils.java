package de.evoila.cf.security.openid;

import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.security.model.CompositeAccessToken;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Collections;

/**
 * @author Johannes Hiemer.
 */
public class OpenIdAuthenticationUtils {

    private final static Logger log = LoggerFactory.getLogger(OpenIdAuthenticationUtils.class);

    private final static String AUTH_CODE = "code";

    private static String getAuthCode(String location) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(location);
        String authCode = null;
        for (NameValuePair queryParam : uriBuilder.getQueryParams())
            if (queryParam.getName().equals(AUTH_CODE))
                authCode = queryParam.getValue();

        return authCode;
    }

    public static CompositeAccessToken getAccessAndRefreshToken(String oauthEndpoint, String code, DashboardClient dashboardClient,
                                                          String redirectUri) throws RestClientException {
        String clientBasicAuth = getClientBasicAuthHeader(dashboardClient.getId(),  dashboardClient.getSecret());
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, clientBasicAuth);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,String> form = new LinkedMultiValueMap<>();
        form.add("response_type", "token");
        form.add("grant_type", "authorization_code");
        form.add("client_id", dashboardClient.getId());
        form.add("client_secret", dashboardClient.getSecret());
        form.add("redirect_uri", redirectUri);
        form.add("code", code);

        ResponseEntity<CompositeAccessToken> token;
        try {
            token = template.exchange(oauthEndpoint + "/token",
                    HttpMethod.POST, new HttpEntity<>(form, headers), CompositeAccessToken.class);
        } catch (Exception ex) {
            if (ex instanceof HttpClientErrorException castedEx) {
                log.debug("Getting or refreshing oauth token failed with code " +
                                +castedEx.getRawStatusCode()
                                + " "
                                + castedEx.getStatusCode()
                                + " and following body:\n"
                                + castedEx.getResponseBodyAsString()
                        , ex);
            } else {
                log.debug("Getting or refreshing oauth token failed.", ex);
            }
            throw ex;
        }

        if (token != null)
            return token.getBody();
        else
            return null;
    }

    private static String getClientBasicAuthHeader(String clientId, String clientSecret) {
        try {
            byte[] autbytes = Base64.encode("%s:%s".formatted(clientId, clientSecret).getBytes("UTF-8"));
            String base64 = new String(autbytes);
            return "Basic %s".formatted(base64);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
