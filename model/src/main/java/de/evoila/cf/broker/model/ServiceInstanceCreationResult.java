/**
 * 
 */
package de.evoila.cf.broker.model;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class ServiceInstanceCreationResult {

	private String internalId;

	private String daschboardUrl;

	public ServiceInstanceCreationResult() {
	}

	public ServiceInstanceCreationResult(String internalId, String daschboardUrl) {
		this.internalId = internalId;
		this.daschboardUrl = daschboardUrl;
	}

	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public String getDaschboardUrl() {
		return daschboardUrl;
	}

	public void setDaschboardUrl(String daschboardUrl) {
		this.daschboardUrl = daschboardUrl;
	}

}
