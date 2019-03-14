package de.evoila.cf.broker.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.interfaces.TranformCatalog;
import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.catalog.plan.Schemas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


/**
 * @author Patrick Weber.
 */
@Component
@Profile("pcf")
@Order(20)
public class PcfSchema implements TranformCatalog {

    private final String SCHEMA_JSON = "schemajson";

    private ObjectMapper objectMapper;

    PcfSchema(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Override
    public void tranform(Catalog catalog, Environment environment, EndpointConfiguration endpointConfiguration) {
        catalog.getServices().forEach(s -> s.getPlans().forEach(this::convert));
    }

    @Override
    public void clean(Catalog catalog, Environment environment, EndpointConfiguration endpointConfiguration) {
        catalog.getServices().forEach(s -> s.getPlans().forEach(this::cleanJson));
    }

    private void convert(Plan plan){
        Map<String, Object> customParameter = plan.getMetadata().getCustomParameters();
        if (customParameter.containsKey(SCHEMA_JSON)) {
            String tsParam = (String) customParameter.get(SCHEMA_JSON);
            try {
                plan.setSchemas(objectMapper.readValue(tsParam, Schemas.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cleanJson(Plan plan){
        Map<String, Object> customParameter = plan.getMetadata().getCustomParameters();
        customParameter.remove(SCHEMA_JSON);
    }
}
