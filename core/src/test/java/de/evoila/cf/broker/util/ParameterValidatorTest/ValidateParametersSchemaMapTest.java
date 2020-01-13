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

class ValidateParametersSchemaMapTest extends BaseTest {
    @BeforeEach
    void setUp() {
        replaceObjectMapperWithSpy();
        readJsonSchema();
        initializeParametersMap();
    }

    @Nested
    class inputValidator {

        @Test
        void schemaNull() {
            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class,
                    () -> ParameterValidator.validateParameters(null, parametersMap)
            );
            assertSame(JSONException.class,
                    exception.getCause()
                            .getClass());
        }

        @Test
        void inputNull() {
            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class,
                    () -> ParameterValidator.validateParameters(schema, null));
            assertSame(JSONException.class,
                    exception.getCause()
                            .getClass());
        }
    }

    @Nested
    class writeValueAsStringThrows {

        @Test
        void jsonExceptionForSchema() throws JsonProcessingException {
            JSONException mockedException = new JSONException("Test");
            when(mockedObjectMapper.writeValueAsString(schema)).thenThrow(mockedException);

            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class,
                    () -> ParameterValidator.validateParameters(schema, parametersMap));
            assertSame(mockedException, exception.getCause());
        }

        @Test
        void jsonExceptionForInput() throws JsonProcessingException {
            JSONException mockedException = new JSONException("Test");
            doReturn("{}").when(mockedObjectMapper).writeValueAsString(schema);
            when(mockedObjectMapper.writeValueAsString(parametersMap)).thenThrow(mockedException);
            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class,
                    () -> ParameterValidator.validateParameters(schema, parametersMap));
            assertSame(mockedException, exception.getCause());
        }

        @Test
        void jsonProcessingExceptionForSchema() throws JsonProcessingException {
            JsonProcessingException mockedException = new JsonProcessingException("Test") {
            };
            when(mockedObjectMapper.writeValueAsString(schema)).thenThrow(mockedException);

            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class,
                    () -> {
                        ParameterValidator.validateParameters(schema,
                                parametersMap);
                    }
            );
            assertSame(mockedException,
                    exception.getCause());
        }

        @Test
        void jsonProcessingExceptionForInput() throws JsonProcessingException {
            JsonProcessingException mockedException = new JsonProcessingException("Test") {
            };
            doReturn("{}").when(mockedObjectMapper).writeValueAsString(schema);
            when(mockedObjectMapper.writeValueAsString(parametersMap)).thenThrow(mockedException);

            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class,
                    () -> ParameterValidator.validateParameters(schema, parametersMap));
            assertSame(mockedException, exception.getCause());
        }
    }

    @Nested
    class CasesForPerformValidation {
        @BeforeEach
        void setUpCasesForPerformValidation() {
            readJsonFiles();
        }

        @Test
        void performValidationThrows() throws JsonProcessingException {
            doReturn(schemaString).when(mockedObjectMapper).writeValueAsString(schema);
            doReturn(invalidJsonString).when(mockedObjectMapper).writeValueAsString(parametersMap);
            assertThrows(ValidationException.class, () -> ParameterValidator.validateParameters(schema, parametersMap));
        }

        @Test
        void performValidationDoesNotThrow() throws JsonProcessingException {
            doReturn(schemaString).when(mockedObjectMapper).writeValueAsString(schema);
            doReturn(validJsonString).when(mockedObjectMapper).writeValueAsString(parametersMap);
            assertDoesNotThrow(() -> ParameterValidator.validateParameters(schema, parametersMap));
        }
    }
}
