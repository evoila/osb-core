package de.evoila.cf.broker.util.ParameterValidatorTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.cf.broker.util.ParameterValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class BaseTest
{
    // mock objects
    ObjectMapper mockedObjectMapper;

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
