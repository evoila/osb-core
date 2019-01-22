package de.evoila.cf.broker.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * @author Johannes Hiemer-
 */
public class ObjectMapperUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static Map<String, Object> convertObjectToMap(Object object) {
        return objectMapper.convertValue(object, Map.class);
    }
}
