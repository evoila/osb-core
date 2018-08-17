package de.evoila.cf.broker.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class TokenHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String tokenFor(String email) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        InputStream inputStream = getResourceStream("/jwt.json");
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");

        DateTime dateTime = new DateTime();
        Map<String, Object> token = objectMapper.readValue(writer.toString(), new TypeReference<HashMap<String, Object>>() {
        });
        token.put("iat", dateTime.getMillis() / 1000);
        token.put("auth_time", dateTime.getMillis() / 1000);
        token.put("exp", dateTime.plusHours(2).getMillis() / 1000);
        token.put("preferred_username", email);
        token.put("email", email);

        String tokenString = objectMapper.writeValueAsString(token);

        InputStream keyInputStream = getResourceStream("/keys/server-pkcs8.pem");
        RSAPrivateKey rsaKey = (RSAPrivateKey) loadPrivateKey(keyInputStream, "RSA");
        RsaSigner rsaSigner = new RsaSigner(rsaKey);
        Jwt jwt = JwtHelper.encode(tokenString, rsaSigner);

        return jwt.getEncoded();
    }

    private static InputStream getResourceStream(String path) {
        return TokenHelper.class.getResourceAsStream(path);
    }

    private static PrivateKey loadPrivateKey(InputStream keyInputStream, String algorithm) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(keyInputStream));

        String line;
        StringBuilder stringBuilder = new StringBuilder(bufferedReader.readLine());
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append("\n" + line);
        }

        String privKeyString = stringBuilder.toString().replace("-----BEGIN PRIVATE KEY-----", "").replace("\n-----END PRIVATE KEY-----", "");

        Base64 b64 = new Base64();
        byte[] decoded = b64.decode(privKeyString);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    public static String tokenWithInvalidSignature() {
        return "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.EkN-DOsnsuRjRO6BxXemmJDm3HbxrbRzXglbN2S4sOkopdU4IsDxTI8jO19W_A4K8ZPJijNLis4EZsHeY559a4DFOd50_OqgHGuERTqYZyuhtF39yxJPAjUESwxk2J5k_4zM3O-vtd1Ghyo4IbqKKSy6J9mTniYJPenn5-HIirE";
    }

}