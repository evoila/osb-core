package de.evoila.cf.broker.interfaces;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import org.springframework.core.env.Environment;
import de.evoila.cf.broker.model.catalog.Catalog;


/**
 * @author Patrick Weber.
 */
public interface TransformCatalog {
    void transform(Catalog catalog, Environment environment, EndpointConfiguration endpointConfiguration);
    void clean(Catalog catalog, Environment environment, EndpointConfiguration endpointConfiguration);
}
