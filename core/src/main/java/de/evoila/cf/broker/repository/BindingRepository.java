package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.ServiceInstanceBinding;

import java.util.List;

/**
 * @author Christian Brinker, Johannes Hiemer.
 */
public interface BindingRepository {

	String getInternalBindingId(String bindingId);

	void addInternalBinding(ServiceInstanceBinding binding);

	boolean containsInternalBindingId(String bindingId);

	void unbindService(String bindingId);

	ServiceInstanceBinding findOne(String bindingId);

    List<ServiceInstanceBinding> getBindingsForServiceInstance (String serviceInstanceId);
}