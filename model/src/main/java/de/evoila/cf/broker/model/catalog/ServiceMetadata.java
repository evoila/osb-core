package de.evoila.cf.broker.model.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceMetadata {

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("longDescription")
    private String longDescription;

    @JsonProperty("providerDisplayName")
    private String providerDisplayName;

    @JsonProperty("documentationUrl")
    private String documentationUrl;

    @JsonProperty("supportUrl")
    private String supportUrl;

    public ServiceMetadata() {
    }

    public ServiceMetadata(String displayName, String imageUrl, String longDescription, String providerDisplayName, String documentationUrl, String supportUrl) {
        this.displayName = displayName;
        this.imageUrl = imageUrl;
        this.longDescription = longDescription;
        this.providerDisplayName = providerDisplayName;
        this.documentationUrl = documentationUrl;
        this.supportUrl = supportUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getProviderDisplayName() {
        return providerDisplayName;
    }

    public void setProviderDisplayName(String providerDisplayName) {
        this.providerDisplayName = providerDisplayName;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }

    public String getSupportUrl() {
        return supportUrl;
    }

    public void setSupportUrl(String supportUrl) {
        this.supportUrl = supportUrl;
    }
}
