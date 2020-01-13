package de.evoila.cf.broker.util.ParameterValidatorTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.catalog.plan.SchemaServiceCreate;
import de.evoila.cf.broker.model.catalog.plan.SchemaServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Schemas;
import de.evoila.cf.broker.util.ParameterValidator;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ValidateParametersServiceInstanceRequestPlanBooleanTest extends BaseTest {
    @Mock
    private Plan mockedPlan;

    @Test
    void serviceInstanceRequestNull() {
        // isUpdate true
        assertThrows(IllegalArgumentException.class,
                () -> ParameterValidator.validateParameters((ServiceInstanceRequest) null, mockedPlan, true));
        // isUpdate false
        assertThrows(IllegalArgumentException.class,
                () -> ParameterValidator.validateParameters((ServiceInstanceRequest) null, mockedPlan, false));
    }

    @Nested
    class serviceInstanceRequestNotNull {
        @Mock
        private Schemas mockedSchemas;
        @Mock
        private SchemaServiceInstance mockedServiceInstance;
        @Mock
        private SchemaServiceCreate mockedServiceCreate;
        @Mock
        private ServiceInstanceRequest mockedServiceInstanceRequest;

        @BeforeEach
        void setUpServiceInstanceRequestNotNull() {
            replaceObjectMapperWithSpy();
            readJsonFiles();
            readJsonSchema();
            initializeParametersMap();

            when(mockedServiceInstanceRequest.getParameters()).thenReturn(parametersMap);
            when(mockedPlan.getSchemas()).thenReturn(mockedSchemas);
            when(mockedSchemas.getServiceInstance()).thenReturn(mockedServiceInstance);
            when(mockedServiceInstance.getCreate()).thenReturn(mockedServiceCreate);
            when(mockedServiceCreate.getParameters()).thenReturn(schema);
        }

        @Test
        void validateParametersThrowsValidationException() throws JsonProcessingException {
            doReturn(schemaString).when(mockedObjectMapper).writeValueAsString(schema);
            doReturn(invalidJsonString).when(mockedObjectMapper).writeValueAsString(parametersMap);
            assertThrows(ValidationException.class,
                    () -> ParameterValidator.validateParameters(mockedServiceInstanceRequest, mockedPlan, false));
        }

        @Test
        void validateParametersThrowsServiceBrokerException() throws JsonProcessingException {
            JSONException mockedException = new JSONException("Test");
            doThrow(mockedException).when(mockedObjectMapper).writeValueAsString(schema);

            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class,
                    () -> ParameterValidator.validateParameters(mockedServiceInstanceRequest, mockedPlan, false));
            assertSame(mockedException, exception.getCause());
        }

        @Test
        void validateParametersDoesNotThrow() throws ServiceBrokerException {
            ParameterValidator.validateParameters(mockedServiceInstanceRequest, mockedPlan, false);
        }
    }
}
