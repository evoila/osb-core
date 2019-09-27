/**
 * 
 */
package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author Johannes Hiemer.
 *
 */
public class DashboardClient {

	private String id;

	private String secret;
	
	@JsonProperty("redirect_uri")
	private String redirectUri;
	
	public DashboardClient() {
	}

	public DashboardClient(String id, String secret, String redirectUri) {
		this.id = id;
		this.secret = secret;
		this.redirectUri = redirectUri;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DashboardClient that = (DashboardClient) o;
		return id.equals(that.id) &&
			   secret.equals(that.secret) &&
			   Objects.equals(redirectUri, that.redirectUri);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, secret, redirectUri);
	}
	
}
