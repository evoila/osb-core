package de.evoila.cf.broker.util;

import java.io.IOException;
import java.util.Map;

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
import de.evoila.cf.broker.model.SchemaParameters;
import de.evoila.cf.broker.model.SchemaProperty;

public class ParameterValidator {
    public static final String JSON_V4_SCHEMA_IDENTIFIER = "http://json-schema.org/draft-04/schema#";
    public static final String JSON_SCHEMA_IDENTIFIER_ELEMENT = "$schema";



    public static JsonNode getJsonNode(Map<String, Object> properties) throws ProcessingException, JsonProcessingException {
       String json = new ObjectMapper().writeValueAsString(properties);
       JsonNode node = null;
       try{
           node = getJsonNode(json);
       }catch (IOException e){
       }
       return node;
    }

    public static JsonSchema getJsonSchema(SchemaParameters schema) throws ProcessingException, JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(schema);
        JsonSchema jsonSchema = null;
        try{
            jsonSchema = getSchemaNode(json);
        }catch (IOException e){
        }
        return jsonSchema;
    }

    public static JsonNode getJsonNode(String jsonText) throws IOException {
        return JsonLoader.fromString(jsonText);
    }

    public static JsonSchema getSchemaNode(String schemaText) throws IOException, ProcessingException {
        final JsonNode schemaNode = getJsonNode(schemaText);
        return _getSchemaNode(schemaNode);
    }


    public static void validateJson(JsonSchema jsonSchemaNode, JsonNode jsonNode) throws ProcessingException {
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