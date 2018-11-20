package de.evoila.cf.broker.model.catalog;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * The catalog of services offered by this broker.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
@Configuration
@ConfigurationProperties(prefix = "catalog")
public class Catalog {

	private List<ServiceDefinition> services = new ArrayList<>();

	public List<ServiceDefinition> getServices() {
		return services;
	}

	public void setServices(List<ServiceDefinition> services) {
		this.services = services;
	}
}
