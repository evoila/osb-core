package de.evoila.cf.broker.model.json.schema.utils;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import de.evoila.cf.broker.model.json.schema.JsonSchema;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


    public static void defaults(JsonSchema jsonSchema, Map<String, Object> createdMap) throws Exception {
        if (jsonSchema.getType() == null) {
            throw new JSONException("JsonSchema Porperty must have a type");
        }
        switch (jsonSchema.getType()){
                case ARRAY:
                        for (Object item:((List)createdMap)) {
                            if (item instanceof Map<?,?>){
                                defaults(jsonSchema.getItems().get(0),(Map<String, Object>) item);
                            }
                        }
                    break;
                case OBJECT:
                    for (Map.Entry<String, JsonSchema> entry : jsonSchema.getProperties().entrySet()) {

                        JsonSchema schemaProperty = entry.getValue();
                        Object defaults = schemaProperty.getDefault();
                        String key = entry.getKey();
                        if (schemaProperty.getType() == null) {
                            throw new JSONException("JsonSchema Porperty must have a type");
                        }
                        switch (schemaProperty.getType()) {
                            case ARRAY:
                                if (createdMap.containsKey(key)) {
                                    defaults(schemaProperty, (Map<String, Object>) createdMap.get(key));
                                }
                                break;
                            case OBJECT:
                                if (createdMap.containsKey(key)) {
                                    defaults(schemaProperty, (Map<String, Object>) createdMap.get(key));
                                } else {
                                    Map<String, Object> tmpResult = new HashMap<String, Object>();
                                    defaults(schemaProperty, tmpResult);
                                    if (!tmpResult.isEmpty()) {
                                        createdMap.put(key, tmpResult);
                                    }
                                }
                                break;
                            case NULL:
                                throw new JSONException("JsonSchema Porperty must have a type");
                            default:
                                if (!createdMap.containsKey(key) && defaults != null) {
                                    createdMap.put(key, defaults);
                                }
                                break;
                        }
                    }
                    break;
                case NULL:
                    throw new JSONException("JsonSchema Porperty must have a type");
                default:
                    break;
        }
    }

}
