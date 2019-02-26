package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.RouteBinding;

/**
 * @author Christian Brinker.
 *
 */
public interface RouteBindingRepository {

	String getRouteBindingId(String bindingId);

	void addRouteBinding(RouteBinding binding);

	boolean containsRouteBindingId(String bindingId);

	void deleteRouteBinding(String bindingId);

	RouteBinding findOne(String bindingId);

}