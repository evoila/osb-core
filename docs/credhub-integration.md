# Credhub Integration

With the credhub integration it is now possible to generate, store and use credentials or certificates on the fly.

---

## 1. Requirements

Before using the feature make sure to save all necessary information in the **application.yml**. These **MUST** be saved as follows:

    spring:
        credhub:
            url: https://<bosh-director>:8844
            oauth2:
                client-id: <client-id>
                client-secret: <client-secret>
                access-token-uri: https://<bosh-director>:8443/oauth/token

### 1.1 Description

| Field                                  | Description   |
| -------------------------------------- | ----------------- |
| spring.credhub.url                     | URL of the environment where credhub is installed |
| spring.credhub.oauth2.client-id        | Username for fetching an oauth token |
| spring.credhub.oauth2.client-secret    | Password for fetching an oauth token |
| spring.credhub.oauth2.access-token-uri | Uri to fetch an oauth token from, which is used to authenticate for generating, storing & deleting credentials |

---

## 2. Generating

The credhub client can either generate an user (consisting of usernamen and password), a password, a json file or a certificate.

```java

public void createUser(String instanceId, String valueName, String username, int passwordLength) { ... }

public void createPassword(String instanceId, String valueName, int passwordLength) { ... }

public void createJson(String intanceId, String valueName, Map<String, Object> values) { ... }

public void createCertificate(String instanceId, String valueName, CertificateParameters certificateParameters) { ... }
```

Credentials are stored as `/<bosh-director-name>/sb-<instanceId>/<valueName>`, for example `/bosh-1/sb-dc4ac700-ee62-4b24-817b-f1b10f2d2d40/redisPassword`.

### 2.1 Description

| Parameter                                  | Description   |
| -------------------------------------- |-----------------|
| instanceId | ID of the service instance|
| username | Username for the user entry only|
| valueName | Name of the value you want to save|
| passwordLength| **(OPTIONAL)** Length of the password, default is 40 |
| values | A map of values should be saved as json |
| certificateParameters | Parameters for a certificate, created by a CertificateParametersBuilder. See [here](https://docs.spring.io/spring-credhub/docs/1.0.0.RELEASE/api/org/springframework/credhub/support/certificate/CertificateParameters.CertificateParametersBuilder.html)|

## 3. Usage

### 3.1 Manifest

The manifest.yml can access credhub credentials as follows

#### 3.1.1 User

    properties:
        ...:
            username: ((<valueName>.username))
            password: ((<valueName>.password))

#### 3.1.2 Password

    properties:
        ...:
            password: ((<valueName>))

#### 3.1.3 JSON

    properties:
        ...:
            anyField: ((<valueName>.<anyValueFromJson>))

#### 3.1.4 Certificates

    properties:
        ...:
            certificate: ((<valueName>.certificate))
            ca: ((<valueName>.ca))
            private_key: ((<valueName>.private_key))

### 3.2 Java

You can also access credhub credentials in java with the following methods:

```java

//User credentials

public String getUserName(String instanceId, String valueName) { ... }

public String getUserPassword(String instanceId, String valueName) { ... }

//Password credentials

public String getPassword(String instanceId, String valueName) { ... }

//JSON credentials

public Object getJson(String instanceId, String valueName, String key) { ... }

//Certificate credentials

public String getCertificate(String instanceId, String valueName) { ... }

public String getCertificateAuthority(String instanceId, String valueName) { ... }

public String getPrivateKey(String instanceId, String valueName) { ... }

```

Parameters are the same as in 2.1

---