package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class Schemas {
	
	@JsonSerialize
	@JsonProperty(value="service_instance", required=false)
	private SchemaServiceInstance serviceInstance;
	
	@JsonSerialize
	@JsonProperty(value="service_binding", required=false)
	private SchemaServiceBinding serviceBinding;
	
	public Schemas() {
		
	}

	public SchemaServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(SchemaServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public SchemaServiceBinding getServiceBinding() {
		return serviceBinding;
	}

	public void setServiceBinding(SchemaServiceBinding serviceBinding) {
		this.serviceBinding = serviceBinding;
	}
	
	

}
