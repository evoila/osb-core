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

    public static Map<String, Object> mergeMaps(Map<String, JsonSchema> schemaProperties,
                                                Map<String, Object> instanceGroupProperties,
                                                Map<String, Object> result)
            throws IllegalArgumentException {

        if (schemaProperties == null) {
            throw new IllegalArgumentException("schemaProperties is null");
        }
        if (instanceGroupProperties == null) {
            throw new IllegalArgumentException("instanceGroupProperties is null");
        }
        if (result == null) {
            throw new IllegalArgumentException("result is null");
        }
        for (Map.Entry<String, JsonSchema> schemaProperty : schemaProperties.entrySet()) {
            if (schemaProperty.getValue().getProperties() != null && !schemaProperty.getValue().getProperties().isEmpty()) {
                Map<String, Object> instanceGroupProperty = null;
                try {
                    instanceGroupProperty = (Map<String, Object>) instanceGroupProperties.get(schemaProperty.getKey());
                } catch (ClassCastException e) {
                    // ignore it
                }
                if (instanceGroupProperty == null) {
                    instanceGroupProperty = new HashMap<>();
                }
                Map<String, Object> innerResultMap = new HashMap<>();
                result.put(schemaProperty.getKey(), innerResultMap);
                mergeMaps(schemaProperty.getValue().getProperties(),
                          instanceGroupProperty,
                          innerResultMap);
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
                        case INTEGER:
                            result.put(schemaProperty.getKey(), 0);
                            break;
                        default:
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
