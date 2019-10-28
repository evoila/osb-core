package de.evoila.cf.broker.util.ParameterValidatorTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstanceUpdateRequest;
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

public class ValidateParametersServiceInstanceUpdateRequestPlanBooleanTest extends BaseTest
{
    @Mock
    private Plan mockedPlan;

    @Test
    void serviceInstanceUpdateRequestNull()
    {
        // isUpdate true
        assertThrows(IllegalArgumentException.class,
                     () -> {
                            ParameterValidator.validateParameters((ServiceInstanceUpdateRequest) null,
                                                                  mockedPlan,
                                                                  true);
                        }
                    );
        // isUpdate false
        assertThrows(IllegalArgumentException.class,
                     () -> {
                            ParameterValidator.validateParameters((ServiceInstanceUpdateRequest) null,
                                                                  mockedPlan,
                                                                  false);
                        }
                    );
    }

    @Nested
    class serviceInstanceUpdateRequestNotNull
    {
        @Mock
        private Schemas                      mockedSchemas;
        @Mock
        private SchemaServiceInstance        mockedServiceInstance;
        @Mock
        private SchemaServiceCreate          mockedServiceCreate;
        @Mock
        private ServiceInstanceUpdateRequest mockedServiceInstanceUpdateRequest;

        @BeforeEach
        void setUpServiceInstanceUpdateRequestNotNull()
        {
            replaceObjectMapperWithSpy();
            readJsonFiles();
            readJsonSchema();
            initializeParametersMap();

            when(mockedServiceInstanceUpdateRequest.getParameters() )
                    .thenReturn(parametersMap);

            when(mockedPlan.getSchemas())
                    .thenReturn(mockedSchemas);
            when(mockedSchemas.getServiceInstance())
                    .thenReturn(mockedServiceInstance);
            when(mockedServiceInstance.getCreate())
                    .thenReturn(mockedServiceCreate);
            when(mockedServiceCreate.getParameters())
                    .thenReturn(schema);
        }

        @Test
        void validateParametersThrowsValidationException() throws JsonProcessingException
        {
            doReturn(schemaString)
                    .when(mockedObjectMapper)
                    .writeValueAsString(schema);
            doReturn(invalidJsonString)
                    .when(mockedObjectMapper)
                    .writeValueAsString(parametersMap);

            assertThrows(ValidationException.class,
                         () -> {
                                ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest,
                                                                      mockedPlan,
                                                                      false);
                            }
                        );
        }

        @Test
        void validateParametersThrowsServiceBrokerException() throws JsonProcessingException
        {
            JSONException mockedException = new JSONException("Test"){};
            doThrow(mockedException)
                    .when(mockedObjectMapper)
                    .writeValueAsString(schema);

            ServiceBrokerException exception = assertThrows(ServiceBrokerException.class,
                                                            () -> {
                                                                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, false);
                                                                }
                                                           );
            assertSame(mockedException,
                       exception.getCause());
        }

        @Test
        void validateParametersDoesNotThrow() throws ServiceBrokerException
        {
            ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest,
                                                  mockedPlan,
                                                  false);
        }
    }
}
