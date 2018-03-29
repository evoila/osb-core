package de.evoila.cf.broker.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The catalog of services offered by this broker.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */

@ConfigurationProperties("catalog")
public class Catalog {

	private List<ServiceDefinition> services = new ArrayList<ServiceDefinition>();
	
	public Catalog() {
		
	}
	
	public Catalog(List<ServiceDefinition> services) {
		this.setServices(services); 
	}
	
	public List<ServiceDefinition> getServices() {
		return services;
	}

	private void setServices(List<ServiceDefinition> services) {
		if ( services == null ) {
<<<<<<< HEAD
			this.services = new ArrayList<ServiceDefinition>();
=======
			this.services = new ArrayList<>();
>>>>>>> fa9995f88f7b8d18ca2a28f93b9861bda220847f
		} else {
			this.services= services;
		} 
	}
}
