package de.evoila.cf.broker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import de.evoila.cf.broker.exception.InvalidParametersException;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.catalog.plan.SchemaParameters;
import de.evoila.cf.broker.model.catalog.plan.SchemaProperty;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParameterValidator {

    public static final String JSON_V4_SCHEMA_IDENTIFIER = "http://json-schema.org/draft-04/schema#";
    public static final String JSON_SCHEMA_IDENTIFIER_ELEMENT = "$schema";

    private static JsonNode getJsonNode(Map<String, Object> properties) throws ProcessingException, JsonProcessingException {
       String json = new ObjectMapper().writeValueAsString(properties);
       JsonNode node = null;
       try{
           node = getJsonNode(json);
       }catch (IOException e){
       }
       return node;
    }

    private static JsonSchema getJsonSchema(SchemaParameters schema) throws ProcessingException, JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(schema);
        JsonSchema jsonSchema = null;
        try{
            jsonSchema = getSchemaNode(json);
        }catch (IOException e){
        }
        return jsonSchema;
    }

    private static JsonNode getJsonNode(String jsonText) throws IOException {
        return JsonLoader.fromString(jsonText);
    }

    private static JsonSchema getSchemaNode(String schemaText) throws IOException, ProcessingException {
        final JsonNode schemaNode = getJsonNode(schemaText);
        return _getSchemaNode(schemaNode);
    }

    public static void validateParameters(ServiceInstanceBindingRequest serviceInstanceBindingRequest, Plan plan) throws InvalidParametersException , ProcessingException{

        /* key validation*/
        HashMap<String, Object> serviceInstanceRequestParams = (HashMap<String, Object>)serviceInstanceBindingRequest.getParameters();

        HashMap<String, SchemaProperty> params = null;
        try{
            params = (HashMap<String, SchemaProperty>)plan.getSchemas().getServiceBinding().getCreate().getParameters().getProperties();
        }catch (NullPointerException e){
            throw new InvalidParametersException("No additional parameters are allowed for this request with this plan");
        }
        boolean flag;
        for (String requestKey : serviceInstanceRequestParams.keySet()) {
            flag = false;
            Iterator<Map.Entry<String, SchemaProperty>> entries = params.entrySet().iterator();
            while(!(flag) && entries.hasNext()){
                Map.Entry<String, SchemaProperty> key = entries.next();
                if(requestKey.equals(key.getKey())){
                    flag = true;
                }
            }
            if(!(flag)){
                throw new InvalidParametersException(serviceInstanceRequestParams);
            }
        }

        /* value validation */
        SchemaParameters json = plan.getSchemas().getServiceBinding().getCreate().getParameters();
        HashMap<String, Object> params2;
        params2 = (HashMap<String, Object>)serviceInstanceBindingRequest.getParameters();

        JsonSchema jsonSchema = null;
        JsonNode jsonObject = null;
        try {
            jsonSchema = getJsonSchema(json);
            jsonObject = getJsonNode(params2);
        }catch (JsonProcessingException e){
            throw new InvalidParametersException("Error while processing json schema");
        }
        try {
            validateJson(jsonSchema, jsonObject);
        }catch (ProcessingException e){
            throw new InvalidParametersException("Error while processing json schema. Values not allowed");
        }
    }
    public static void validateParameters(ServiceInstanceRequest serviceInstanceRequest, Plan plan) throws InvalidParametersException, ProcessingException{

        /* key validation*/
        HashMap<String, Object> serviceInstanceRequestParams = (HashMap<String, Object>)serviceInstanceRequest.getParameters();

        HashMap<String, SchemaProperty> params = null;
        try{
            params = (HashMap<String, SchemaProperty>)plan.getSchemas().getServiceInstance().getCreate().getParameters().getProperties();
        }catch (NullPointerException e){
            throw new InvalidParametersException("No additional parameters are allowed for this request with this plan");
        }
        boolean flag;
        for (String requestKey : serviceInstanceRequestParams.keySet()) {
            flag = false;
            Iterator<Map.Entry<String, SchemaProperty>> entries = params.entrySet().iterator();
            while(!(flag) && entries.hasNext()){
                Map.Entry<String, SchemaProperty> key = entries.next();
                if(requestKey.equals(key.getKey())){
                    flag = true;
                }
            }
            if(!(flag)){
                throw new InvalidParametersException(serviceInstanceRequestParams);
            }
        }

        /* schema validation */
        SchemaParameters json = plan.getSchemas().getServiceInstance().getCreate().getParameters();
        HashMap<String, Object> params2;
        params2 = (HashMap<String, Object>)serviceInstanceRequest.getParameters();

        JsonSchema jsonSchema = null;
        JsonNode jsonObject = null;
        try {
            jsonSchema = getJsonSchema(json);
            jsonObject = getJsonNode(params2);
        }catch (JsonProcessingException e){
            throw new InvalidParametersException("Error while processing json schema");
        }
        try {
            validateJson(jsonSchema, jsonObject);
        }catch (ProcessingException e){
            throw new InvalidParametersException("Error while processing json schema. Values not allowed");
        }
    }

    private static void validateJson(JsonSchema jsonSchemaNode, JsonNode jsonNode) throws ProcessingException {
        ProcessingReport report = jsonSchemaNode.validate(jsonNode);
        if (!report.isSuccess()) {
            for (ProcessingMessage processingMessage : report) {
                throw new ProcessingException(processingMessage);
            }
        }
    }

    private static JsonSchema _getSchemaNode(JsonNode jsonNode) throws ProcessingException {
        final JsonNode schemaIdentifier = jsonNode.get(JSON_SCHEMA_IDENTIFIER_ELEMENT);
        if (schemaIdentifier == null){ //even if null, seems not to go into the if statement
            ((ObjectNode) jsonNode).put(JSON_SCHEMA_IDENTIFIER_ELEMENT, JSON_V4_SCHEMA_IDENTIFIER);
        }
        ((ObjectNode) jsonNode).put(JSON_SCHEMA_IDENTIFIER_ELEMENT, JSON_V4_SCHEMA_IDENTIFIER);
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        return factory.getJsonSchema(jsonNode);
    }
}