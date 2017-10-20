/**
 *
 */
package de.evoila.cf.cpi.custom.props;

import java.util.Map;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
//import de.evoila.cf.cpi.custom.props.DomainBasedCustomPropertyHandler;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class CouchDBCustomPropertyHandler extends DefaultDatabaseCustomPropertyHandler {

	@Override
	public Map<String, String> addDomainBasedCustomProperties(Plan plan, Map<String, String> customProperties, ServiceInstance serviceInstance) {
		return super.addDomainBasedCustomProperties(plan, customProperties, serviceInstance);
	}
}


