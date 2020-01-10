package de.evoila.cf.broker.model.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;


@JsonInclude(JsonInclude.Include.NON_NULL)
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

    private boolean shareable = true;

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

    public void setShareable(boolean shareable) {
        this.shareable = shareable;
    }

    public boolean isShareable() {
        return shareable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ServiceMetadata that = (ServiceMetadata) o;
        return Objects.equals(displayName, that.displayName) &&
               Objects.equals(imageUrl, that.imageUrl) &&
               Objects.equals(longDescription, that.longDescription) &&
               Objects.equals(providerDisplayName, that.providerDisplayName) &&
               Objects.equals(documentationUrl, that.documentationUrl) &&
               Objects.equals(supportUrl, that.supportUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, imageUrl, longDescription, providerDisplayName, documentationUrl, supportUrl);
    }

}
