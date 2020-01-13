package de.evoila.cf.broker.util.ParameterValidatorTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.evoila.cf.broker.util.ParameterValidator;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class SerializeObjectToJsonObjectTest extends BaseTest {

    @BeforeEach
    void setUp() {
        replaceObjectMapperWithSpy();
    }

    @Test
    void writeValuesAsStringThrowsJsonProcessingException() throws JsonProcessingException {
        JsonProcessingException mockedException = new JsonProcessingException("Test") {
        };
        when(mockedObjectMapper.writeValueAsString(any(Object.class))).thenThrow(mockedException);
        JsonProcessingException thrownException = assertThrows(JsonProcessingException.class,
                () -> ParameterValidator.serializeObjectToJSONObject(new Object()));
        assertSame(mockedException, thrownException);
    }

    @Test
    void writeValuesAsStringThrowsJsonException() throws JsonProcessingException {
        JSONException mockedException = new JSONException("Test");
        when(mockedObjectMapper.writeValueAsString(any(Object.class))).thenThrow(mockedException);
        JSONException thrownException = assertThrows(JSONException.class,
                () -> ParameterValidator.serializeObjectToJSONObject(new Object()));
        assertSame(mockedException, thrownException);
    }

    @Test
    void writeValueAsStringDoesNotThrow() throws JsonProcessingException {
        doReturn("{}").when(mockedObjectMapper).writeValueAsString(any(Object.class));
        JSONObject newObject = ParameterValidator.serializeObjectToJSONObject(new Object());
        assertNotNull(newObject);
    }
}

