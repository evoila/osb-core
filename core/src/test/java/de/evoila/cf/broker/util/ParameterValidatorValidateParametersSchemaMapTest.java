package de.evoila.cf.broker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.json.schema.JsonSchema;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParameterValidatorValidateParametersSchemaMapTest
{
    // paths to resource files
    private final String SCHEMA_PATH = Path.of(".", "src", "test", "resources", "ParameterValidator", "testJsonSchema.json").toString();
    private final String VALID_JSON_PATH = Path.of(".", "src", "test", "resources", "ParameterValidator", "validJson.json").toString();
    private final String INVALID_JSON_PATH = Path.of(".", "src", "test", "resources", "ParameterValidator", "invalidJson.json").toString();

    // mock objects
    private ObjectMapper mockedObjectMapper;
    // data read from resource files
    private JsonSchema schema;
    private String schemaString;
    private String validJsonString;
    private String invalidJsonString;
    // input data for methods under testing
    private Map<String, Object> inputMap;

    /**
     * Reads a file and stores its content as a string.
     * @param file  The file that shall be read.
     * @return  A string containing the data read from the file. Never returns null.
     */
    private String readFileAsString(File file)
    {
        String fileAsString = new String();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                fileAsString += line;
            }
        } catch (IOException e) {
            throw new RuntimeException("Loading test JSON schema failed", e);
        }

        return fileAsString;
    }

    /**
     * Replaces the object mapper in the class that is tested with a Mockito.spy object.
     */
    private void replaceObjectMapperWithSpy()
    {
        mockedObjectMapper = spy(new ObjectMapper());
        try {
            FieldSetter.setField(ParameterValidator.class,
                                    ParameterValidator.class.getDeclaredField("objectMapper"),
                                    mockedObjectMapper);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Setting field failed", e);
        }
    }

    /**
     * Reads the JSON schema that is used for testing and stores it as JsonSchema object and as string.
     */
    private void readJsonSchema()
    {
        // store Json schema in JsonSchema object
        File file = new File(SCHEMA_PATH);
        ObjectMapper mapper = new ObjectMapper();
        try {
            schema = mapper.readValue(file, JsonSchema.class);
        } catch (IOException e) {
            throw new RuntimeException("Loading test JSON schema failed", e);
        }

        // store Json schema as string
        schemaString = readFileAsString(file);
    }

    /**
     * Reads the JSON files that are used for these tests and stores them as strings.
     */
    private void readJsonFiles()
    {
        validJsonString = readFileAsString(new File(VALID_JSON_PATH));
        invalidJsonString = readFileAsString(new File(INVALID_JSON_PATH));
    }

    /**
     * Initializes the member 'inputMap'. It contains the data from validJson.json.
     */
    private void initializeInputMap()
    {
        inputMap = new HashMap<>();
        inputMap.put("price", 2);
        inputMap.put("name", "randomName");
    }

    @BeforeEach
    void setUp()
    {
        replaceObjectMapperWithSpy();
        readJsonSchema();
        initializeInputMap();
        readJsonFiles();
    }

    @Nested
    class inputValidator {
        @Test
        void schemaNull() {
            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                        ParameterValidator.validateParameters(null, inputMap);
                    }
            );
            assertSame(JSONException.class, exception.getCause().getClass());
        }

        @Test
        void inputNull() {
            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                        ParameterValidator.validateParameters(schema, null);
                    }
            );
            assertSame(JSONException.class, exception.getCause().getClass());
        }
    }

    @Nested
    class writeValueAsStringThrows {
        @Test
        void jsonExceptionForSchema() throws JsonProcessingException {
            JSONException mockedException = new JSONException("Test"){};
            when(mockedObjectMapper.writeValueAsString(schema))
                    .thenThrow(mockedException);

            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                        ParameterValidator.validateParameters(schema, inputMap);
                    }
            );
            assertSame(mockedException, exception.getCause());
        }

        @Test
        void jsonExceptionForInput() throws JsonProcessingException {
            JSONException mockedException = new JSONException("Test"){};
            doReturn("{}")
                    .when(mockedObjectMapper)
                    .writeValueAsString(schema);
            when(mockedObjectMapper.writeValueAsString(inputMap))
                    .thenThrow(mockedException);

            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                        ParameterValidator.validateParameters(schema, inputMap);
                    }
            );
            assertSame(mockedException, exception.getCause());
        }

        @Test
        void jsonProcessingExceptionForSchema() throws JsonProcessingException {
            JsonProcessingException mockedException = new JsonProcessingException("Test") {};
            when(mockedObjectMapper.writeValueAsString(schema))
                    .thenThrow(mockedException);

            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                        ParameterValidator.validateParameters(schema, inputMap);
                    }
            );
            assertSame(mockedException, exception.getCause());
        }

        @Test
        void jsonProcessingExceptionForInput() throws JsonProcessingException {
            JsonProcessingException mockedException = new JsonProcessingException("Test") {};
            doReturn("{}")
                    .when(mockedObjectMapper)
                    .writeValueAsString(schema);
            when(mockedObjectMapper.writeValueAsString(inputMap))
                    .thenThrow(mockedException);

            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                        ParameterValidator.validateParameters(schema, inputMap);
                    }
            );
            assertSame(mockedException, exception.getCause());
        }
    }

    @Nested
    class CasesForPerformValidation
    {
        @Test
        void performValidationThrows() throws JsonProcessingException {
            doReturn(schemaString)
                    .when(mockedObjectMapper)
                    .writeValueAsString(schema);
            doReturn(invalidJsonString)
                    .when(mockedObjectMapper)
                    .writeValueAsString(inputMap);

            assertThrows(ValidationException.class, () -> {
                        ParameterValidator.validateParameters(schema, inputMap);
                    }
            );
        }

        @Test
        void performValidationDoesNotThrow() throws JsonProcessingException {
            doReturn(schemaString)
                    .when(mockedObjectMapper)
                    .writeValueAsString(schema);
            doReturn(validJsonString)
                    .when(mockedObjectMapper)
                    .writeValueAsString(inputMap);

            assertDoesNotThrow(() -> {ParameterValidator.validateParameters(schema, inputMap);} );
        }
    }
}
