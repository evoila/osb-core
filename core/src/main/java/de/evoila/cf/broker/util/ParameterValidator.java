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
                                          boolean isUpdate) throws ValidationException, ServiceBrokerException, IllegalArgumentException {
        if (serviceInstanceBindingRequest == null) {
            throw new IllegalArgumentException("Parameter ServiceInstanceBindingRequest is null");
        }
        if (planHasBindingSchema(plan, isUpdate) == false) {
            return;
        }
        JsonSchema jsonSchema = null;
        if (isUpdate) {
            jsonSchema = plan.getSchemas()
                             .getServiceBinding().getUpdate().getParameters();
        } else {
            jsonSchema = plan.getSchemas()
                             .getServiceBinding().getCreate().getParameters();
        }

        if (jsonSchema != null) {
            validateParameters(jsonSchema, serviceInstanceBindingRequest.getParameters());
        }
    }

    public static void validateParameters(ServiceInstanceRequest serviceInstanceRequest, Plan plan,
                                          boolean isUpdate) throws ValidationException, ServiceBrokerException, IllegalArgumentException {
        if (serviceInstanceRequest == null) {
            throw new IllegalArgumentException("Parameter ServiceInstanceRequest is null");
        }

        validateParameters(serviceInstanceRequest.getParameters(), plan, isUpdate);
    }

    public static void validateParameters(ServiceInstanceUpdateRequest serviceInstanceUpdateRequest, Plan plan,
                                          boolean isUpdate) throws ValidationException, ServiceBrokerException, IllegalArgumentException {
        if (serviceInstanceUpdateRequest == null) {
            throw new IllegalArgumentException("Parameter ServiceInstanceUpdateRequest is null");
        }

        validateParameters(serviceInstanceUpdateRequest.getParameters(), plan, isUpdate);
    }

    public static void validateParameters(Map<String, Object> input, Plan plan,
                                          boolean isUpdate) throws ValidationException, ServiceBrokerException {
        if (planHasInstanceSchema(plan, isUpdate) == false) {
            return;
        }
        JsonSchema jsonSchema = null;
        if (isUpdate) {
            jsonSchema = plan.getSchemas()
                    .getServiceInstance().getUpdate().getParameters();
        } else {
            jsonSchema = plan.getSchemas()
                    .getServiceInstance().getCreate().getParameters();
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
        if (plan != null && plan.getSchemas() != null) {
            Schemas schemas = plan.getSchemas();
            if (schemas.getServiceBinding() != null) {
                SchemaServiceBinding schemaServiceBinding = schemas.getServiceBinding();
                if (isUpdate) {
                    SchemaServiceUpdate serviceUpdate = schemaServiceBinding.getUpdate();
                    return serviceUpdate != null && serviceUpdate.getParameters() != null;
                } else {
                    SchemaServiceCreate serviceCreate = schemaServiceBinding.getCreate();
                    return serviceCreate != null && serviceCreate.getParameters() != null;
                }
            }
        }
        return false;
    }

    private static boolean planHasInstanceSchema(Plan plan, boolean isUpdate) {
        if (plan != null && plan.getSchemas() != null) {
            Schemas schemas = plan.getSchemas();
            if (schemas.getServiceInstance() != null) {
                SchemaServiceInstance serviceInstance = schemas.getServiceInstance();
                if (isUpdate) {
                    return serviceInstance.getUpdate() != null && serviceInstance.getUpdate().getParameters() != null;
                } else {
                    return serviceInstance.getCreate() != null && serviceInstance.getCreate().getParameters() != null;
                }
            }
        }
        return false;
    }
}
