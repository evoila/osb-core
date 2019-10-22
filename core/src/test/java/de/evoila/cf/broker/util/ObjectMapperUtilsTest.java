package de.evoila.cf.broker.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.catalog.plan.Plan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ObjectMapperUtilsTest {
    private static final Path resourcePath = Path.of(".",
                                                     "src",
                                                     "test",
                                                     "resources",
                                                     "ObjectMapperUtils");
    private static final String FILE_PLAN                       = "plan.json";
    private static final String FILE_SERVICE_INSTANCE_BINDING = "serviceInstanceBinding.json";

    @SuppressWarnings("InnerClassMayBeStatic")
    @Nested
    class convertObjectToMapMethod {

        @Test
        void withNull() {
            Map<String, Object> result = ObjectMapperUtils.convertObjectToMap(null);
            assertNull(result);
        }

        private <T> void testConversion(String fileName, Class<T> clazz) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            T object = objectMapper.readValue(resourcePath.resolve(fileName).toFile(),
                                              clazz);
            Map expectedResult = objectMapper.convertValue(object, Map.class);
            Map<String, Object> result = ObjectMapperUtils.convertObjectToMap(object);
            assertEquals(expectedResult, result);
        }

        @Test
        void withPlan() throws IOException {
            testConversion(FILE_PLAN, Plan.class);
        }

        @Test
        void withServiceInstanceBinding() throws IOException {
            testConversion(FILE_SERVICE_INSTANCE_BINDING, ServiceInstanceBinding.class);
        }

    }

}
