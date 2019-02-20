package de.evoila.cf.broker.bean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by reneschollmeyer, evoila on 29.10.18.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.credhub")
@ConditionalOnProperty(prefix = "spring.credhub", name = {"url", "bosh-director",
                                                          "certificate.ca", "certificate.cert", "certificate.private-key"})
public class CredhubBean {

    private String url;

    private String boshDirector;


    private Certificate certificate;

    private String keystorePassword = "";


    public String getBoshDirector() {
        return boshDirector;
    }

    public void setBoshDirector(String boshDirector) {
        this.boshDirector = boshDirector;
    }


    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public static class Certificate {
        private String ca;

        private String cert;

        private String privateKey;

        public String getCa() {
            return ca;
        }

        public void setCa(String ca) {
            this.ca = ca;
        }

        public String getCert() {
            return cert;
        }

        public void setCert(String cert) {
            this.cert = cert;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }
}
