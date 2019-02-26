package de.evoila.cf.broker.model.credential;

/**
 * @author Johannes Hiemer.
 */
public class CertificateCredential extends Credential {

    private String certificate;

    private String certificateAuthority;

    private String privateKey;

    public CertificateCredential() {}

    public CertificateCredential(String certificate, String certificateAuthority, String privateKey) {
        this.certificate = certificate;
        this.certificateAuthority = certificateAuthority;
        this.privateKey = privateKey;
    }

    public CertificateCredential(String id, String certificateAuthority, String certificate, String privateKey) {
        this.id = id;
        this.certificateAuthority = certificateAuthority;
        this.certificate = certificate;
        this.privateKey = privateKey;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getCertificateAuthority() {
        return certificateAuthority;
    }

    public void setCertificateAuthority(String certificateAuthority) {
        this.certificateAuthority = certificateAuthority;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
