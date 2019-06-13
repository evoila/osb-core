package de.evoila.cf.broker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.ServiceInstanceUpdateRequest;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.json.schema.JsonSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.Validator;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Marco Di Martion, Johannes Hiemer.
 */
public class ParameterValidator {

    static ObjectMapper objectMapper = new ObjectMapper();

    public static void validateParameters(ServiceInstanceBindingRequest serviceInstanceBindingRequest, Plan plan,
                                          boolean isUpdate) throws ValidationException, ServiceBrokerException {
        Map<String, Object> serviceInstanceRequestParams = serviceInstanceBindingRequest.getParameters();

        JsonSchema jsonSchema = null;
        if (!isUpdate && planHasBindingSchema(plan, isUpdate)) {
            jsonSchema = plan.getSchemas()
                    .getServiceBinding().getCreate().getParameters();
        } else if (planHasBindingSchema(plan, isUpdate)) {
            jsonSchema = plan.getSchemas()
                    .getServiceBinding().getUpdate().getParameters();
        }
        if (jsonSchema != null) {
            validateParameters(jsonSchema, serviceInstanceRequestParams);
        }
    }

    public static void validateParameters(ServiceInstanceRequest serviceInstanceRequest, Plan plan,
                                          boolean isUpdate) throws ValidationException, ServiceBrokerException {
        Map<String, Object> serviceInstanceRequestParams = serviceInstanceRequest.getParameters();

        JsonSchema jsonSchema = null;

        if (!isUpdate && planHasInstanceSchema(plan, isUpdate)) {
            jsonSchema = plan.getSchemas()
                    .getServiceInstance().getCreate().getParameters();
        } else if (planHasInstanceSchema(plan, isUpdate)) {
            jsonSchema = plan.getSchemas()
                    .getServiceInstance().getUpdate().getParameters();
        }

        if (jsonSchema != null) {
            validateParameters(jsonSchema, serviceInstanceRequestParams);
        }
    }

    public static void validateParameters(ServiceInstanceUpdateRequest serviceInstanceUpdateRequest, Plan plan,
                                          boolean isUpdate) throws ValidationException, ServiceBrokerException {
        Map<String, Object> serviceInstanceRequestParams = serviceInstanceUpdateRequest.getParameters();

        JsonSchema jsonSchema = null;

        if (!isUpdate && planHasInstanceSchema(plan, isUpdate)) {
            jsonSchema = plan.getSchemas()
                    .getServiceInstance().getCreate().getParameters();
        } else if (planHasInstanceSchema(plan, isUpdate)) {
            jsonSchema = plan.getSchemas()
                    .getServiceInstance().getUpdate().getParameters();
        }

        if (jsonSchema != null) {
            validateParameters(jsonSchema, serviceInstanceRequestParams);
        }
    }

    public static void validateParameters(Map<String, Object> input, Plan plan,
                                          boolean isUpdate) throws ValidationException, ServiceBrokerException {
        JsonSchema jsonSchema = null;
        if (!isUpdate && planHasInstanceSchema(plan, isUpdate)) {
            jsonSchema = plan.getSchemas()
                    .getServiceInstance().getCreate().getParameters();
        } else if (planHasInstanceSchema(plan, isUpdate)) {
            jsonSchema = plan.getSchemas()
                    .getServiceInstance().getUpdate().getParameters();
        }

        if (jsonSchema != null) {
            validateParameters(jsonSchema, input);
        }
    }

    public static void validateParameters(JsonSchema jsonSchema, Map<String, Object> input) throws ValidationException,
            ServiceBrokerException {
        try {
            SchemaLoader loader = SchemaLoader.builder()
                    .schemaJson(serializeObjectToJSONObject(jsonSchema))
                    .build();
            Schema schema = loader.load().build();

            Validator validator = Validator.builder()
                    .build();
            validator.performValidation(schema, serializeObjectToJSONObject(input));
        } catch(JsonProcessingException | JSONException ex) {
            throw new ServiceBrokerException("Could not read objects from jsonSchema or input, malformed JSON", ex);
        }
    }

    public static JSONObject serializeObjectToJSONObject(Object value) throws JsonProcessingException, JSONException {
        return new JSONObject(objectMapper.writeValueAsString(value));
    }

    private static boolean planHasBindingSchema(Plan plan, boolean isUpdate) {
        try {
            if (isUpdate) {
                plan.getSchemas().getServiceBinding().getUpdate().getParameters();
            } else {
                plan.getSchemas().getServiceBinding().getCreate().getParameters();
            }
        } catch(Exception e) {
            return false;
        }

        return true;
    }

    private static boolean planHasInstanceSchema(Plan plan, boolean isUpdate) {
        try {
            if (isUpdate) {
                plan.getSchemas().getServiceInstance().getUpdate().getParameters();
            } else {
                plan.getSchemas().getServiceInstance().getCreate().getParameters();
            }
        } catch(Exception e) {
            return false;
        }

        return true;
    }
}
