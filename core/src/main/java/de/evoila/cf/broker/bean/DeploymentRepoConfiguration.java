package de.evoila.cf.broker.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** @author Yannic Remmet */
@Configuration
@ConfigurationProperties(prefix="deployment.repo")
public class DeploymentRepoConfiguration {

	private String service;
	
	private String monit;

	public String getService() {
		return service;
	}

	public String getMonit() {
		return monit;
	}

	public void setService(String service) {
		this.service = service;
	}

	public void setMonit(String monit) {
		this.monit = monit;
	}
}
