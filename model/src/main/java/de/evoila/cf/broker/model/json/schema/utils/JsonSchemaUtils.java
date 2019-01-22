package de.evoila.cf.broker.model.json.schema.utils;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import de.evoila.cf.broker.model.json.schema.JsonSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
public class JsonSchemaUtils {

    public static Map<String, Object> mergeMaps(Map<String, JsonSchema> schemaProperties, Map<String, Object> instanceGroupProperties,
                                          Map<String, Object> result) {

        for (Map.Entry<String, JsonSchema> schemaProperty : schemaProperties.entrySet()) {
            if (schemaProperty.getValue().getProperties() != null && !schemaProperty.getValue().getProperties().isEmpty()) {
                result.put(schemaProperty.getKey(), new HashMap<String, Object>());

                Map<String, Object> instanceGroupProperty = (Map<String, Object>) instanceGroupProperties.get(schemaProperty.getKey());
                if (instanceGroupProperty == null)
                    instanceGroupProperty = new HashMap<>();

                mergeMaps(schemaProperty.getValue().getProperties(),
                        instanceGroupProperty,
                        (Map<String, Object>) result.get(schemaProperty.getKey()));
            } else {
                if (!instanceGroupProperties.containsKey(schemaProperty.getKey())) {
                    JsonFormatTypes type = schemaProperty.getValue().getType();
                    switch (type) {
                        case ARRAY:
                            result.put(schemaProperty.getKey(), new ArrayList());
                            break;
                        case STRING:
                            result.put(schemaProperty.getKey(), "");
                            break;
                        case BOOLEAN:
                            result.put(schemaProperty.getKey(), false);
                            break;
                        case NUMBER:
                            result.put(schemaProperty.getKey(), 0);
                            break;
                        case INTEGER:
                            result.put(schemaProperty.getKey(), 0);
                            break;
                    }
                } else {
                    result.put(schemaProperty.getKey(), instanceGroupProperties.get(schemaProperty.getKey()));
                }
            }
        }
        return result;
    }
}
