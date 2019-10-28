package de.evoila.cf.broker.util.ParameterValidatorTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.util.ParameterValidator;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class ValidateParametersSchemaMapTest extends BaseTest
{
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
