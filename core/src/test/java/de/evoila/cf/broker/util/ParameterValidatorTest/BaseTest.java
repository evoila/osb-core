package de.evoila.cf.broker.util.ParameterValidatorTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.cf.broker.model.json.schema.JsonSchema;
import de.evoila.cf.broker.util.ParameterValidator;
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

import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class BaseTest
{
    // paths to resource files
    private final String SCHEMA_PATH = Path.of(".", "src", "test", "resources", "ParameterValidator", "testJsonSchema.json").toString();
    private final String VALID_JSON_PATH = Path.of(".", "src", "test", "resources", "ParameterValidator", "validJson.json").toString();
    private final String INVALID_JSON_PATH = Path.of(".", "src", "test", "resources", "ParameterValidator", "invalidJson.json").toString();

    // mock objects
    ObjectMapper mockedObjectMapper;

    // data read from resource files
    JsonSchema schema;
    String     schemaString;
    String     validJsonString;
    String     invalidJsonString;

    // input data for methods under testing
    Map<String, Object> inputMap;

    /**
     * Reads a file and stores its content as a string.
     * @param file  The file that shall be read.
     * @return  A string containing the data read from the file. Never returns null.
     */
    String readFileAsString(File file)
    {
        String fileAsString = new String();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String         line;
            while ((line = reader.readLine()) != null) {
                fileAsString += line;
            }
        } catch (IOException e) {
            throw new RuntimeException("Loading test JSON schema failed", e);
        }

        return fileAsString;
    }

    /**
     * Reads the JSON schema that is used for testing and stores it as JsonSchema object and as string.
     */
    void readJsonSchema()
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
    void readJsonFiles()
    {
        validJsonString = readFileAsString(new File(VALID_JSON_PATH));
        invalidJsonString = readFileAsString(new File(INVALID_JSON_PATH));
    }

    /**
     * Initializes the member 'inputMap'. It contains the data from validJson.json.
     */
    void initializeInputMap()
    {
        inputMap = new HashMap<>();
        inputMap.put("price", 2);
        inputMap.put("name", "randomName");
    }

    /**
     * Replaces the object mapper in the class that is tested with a Mockito.spy object.
     */
    void replaceObjectMapperWithSpy()
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
}
