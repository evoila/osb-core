package de.evoila.cf.broker.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * The catalog of services offered by this broker.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
@Configuration
@ConfigurationProperties(prefix = "catalog")
@ConditionalOnProperty(prefix = "catalog", name = {"name", "services"}, havingValue = "")
public class Catalog {

    public Catalog() {
        System.out.println("oidjsvosvijsvio");
    }

    private String name;

	private List<ServiceDefinition> services = new ArrayList<>();

	public List<ServiceDefinition> getServices() {
		return services;
	}

	public void setServices(List<ServiceDefinition> services) {
		this.services = services;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
