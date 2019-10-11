package de.evoila.cf.broker.model.catalog;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		Catalog catalog = (Catalog) o;
		return services.equals(catalog.services);
	}

	@Override
	public int hashCode() {
		return Objects.hash(services);
	}

}
