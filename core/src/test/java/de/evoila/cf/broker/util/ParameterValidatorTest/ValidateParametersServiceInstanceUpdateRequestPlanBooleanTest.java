package de.evoila.cf.broker.util.ParameterValidatorTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstanceUpdateRequest;
import de.evoila.cf.broker.model.catalog.plan.*;
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
    private Plan                         mockedPlan;
    @Mock
    private Schemas                      mockedSchemas;
    @Mock
    private SchemaServiceInstance        mockedServiceInstance;
    @Mock
    private ServiceInstanceUpdateRequest mockedServiceInstanceUpdateRequest;

    @BeforeEach
    void setUpValidateParameters()
    {
        readJsonSchema();
        initializeInputMap();
    }

    @Test
    void serviceInstanceUpdateRequestNull()
    {
        // isUpdate true
        assertThrows(IllegalArgumentException.class, () -> {
                ParameterValidator.validateParameters((ServiceInstanceUpdateRequest) null, mockedPlan, true);
            }
        );
        // isUpdate false
        assertThrows(IllegalArgumentException.class, () -> {
                 ParameterValidator.validateParameters((ServiceInstanceUpdateRequest) null, mockedPlan, false);
             }
        );
    }

    @Nested
    class isUpdateFalse
    {
        @BeforeEach
        void setUpIsUpdateFalse()
        {
            when(mockedServiceInstanceUpdateRequest.getParameters() )
                    .thenReturn(inputMap);
        }

        @Test
        void planNull() throws ServiceBrokerException
        {
            ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, null, false);
        }

        @Nested
        class mocksOfGettersOnPlan
        {
            @Mock
            private SchemaServiceCreate mockedServiceCreate;

            @Nested
            class gettersReturnNull
            {
                @Test
                void getSchemasReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, false);
                }

                @Test
                void getServiceInstanceReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceInstance())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, false);
                }

                @Test
                void getCreateReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceInstance())
                            .thenReturn(mockedServiceInstance);
                    when(mockedServiceInstance.getCreate())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, false);
                }

                @Test
                void getParametersReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceInstance())
                            .thenReturn(mockedServiceInstance);
                    when(mockedServiceInstance.getCreate())
                            .thenReturn(mockedServiceCreate);
                    when(mockedServiceCreate.getParameters())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, false);
                }

            }

            @Nested
            class gettersReturnNotNull
            {
                @BeforeEach
                void setUpGettersReturnNotNull()
                {
                    replaceObjectMapperWithSpy();
                    readJsonFiles();

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
                void validateParametersThrowsServiceBrokerException() throws JsonProcessingException
                {
                    JSONException mockedException = new JSONException("Test"){};
                    doThrow(mockedException)
                            .when(mockedObjectMapper)
                            .writeValueAsString(schema);

                    ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                                                                        ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, false);
                                                                    }
                                                                   );
                    assertSame(mockedException, exception.getCause());
                }

                @Test
                void validateParametersThrowsValidationException() throws JsonProcessingException
                {
                    doReturn(schemaString)
                            .when(mockedObjectMapper)
                            .writeValueAsString(schema);
                    doReturn(invalidJsonString)
                            .when(mockedObjectMapper)
                            .writeValueAsString(inputMap);

                    assertThrows(ValidationException.class, () -> {
                                     ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, false);
                                 }
                                );
                }

                @Test
                void validateParametersDoesNotThrow() throws ServiceBrokerException
                {
                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, false);
                }
            }
        }
    }

    @Nested
    class isUpdateTrue
    {
        @Test
        void planNull() throws ServiceBrokerException
        {
            ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, null, true);
        }

        @BeforeEach
        void setUpIsUpdateTrue()
        {
            when(mockedServiceInstanceUpdateRequest.getParameters() )
                    .thenReturn(inputMap);
        }

        @Nested
        class mocksOfGettersOnPlan
        {
            @Mock
            private SchemaServiceUpdate   mockedServiceUpdate;

            @Nested
            class gettersReturnNull
            {
                @Test
                void getSchemasReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, true);
                }

                @Test
                void getServiceInstanceReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceInstance())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, true);
                }

                @Test
                void getCreateReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceInstance())
                            .thenReturn(mockedServiceInstance);
                    when(mockedServiceInstance.getUpdate())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, true);
                }

                @Test
                void getParametersReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceInstance())
                            .thenReturn(mockedServiceInstance);
                    when(mockedServiceInstance.getUpdate())
                            .thenReturn(mockedServiceUpdate);
                    when(mockedServiceUpdate.getParameters())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, true);
                }

            }

            @Nested
            class gettersReturnNotNull
            {
                @BeforeEach
                void setUpGettersReturnNotNull()
                {
                    replaceObjectMapperWithSpy();
                    readJsonFiles();

                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceInstance())
                            .thenReturn(mockedServiceInstance);
                    when(mockedServiceInstance.getUpdate())
                            .thenReturn(mockedServiceUpdate);
                    when(mockedServiceUpdate.getParameters())
                            .thenReturn(schema);
                }

                @Test
                void validateParametersThrowsServiceBrokerException() throws JsonProcessingException
                {
                    JSONException mockedException = new JSONException("Test"){};
                    doThrow(mockedException)
                            .when(mockedObjectMapper)
                            .writeValueAsString(schema);

                    ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                                                                        ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, true);
                                                                    }
                                                                   );
                    assertSame(mockedException, exception.getCause());
                }

                @Test
                void validateParametersThrowsValidationException() throws JsonProcessingException
                {
                    doReturn(schemaString)
                            .when(mockedObjectMapper)
                            .writeValueAsString(schema);
                    doReturn(invalidJsonString)
                            .when(mockedObjectMapper)
                            .writeValueAsString(inputMap);

                    assertThrows(ValidationException.class, () -> {
                             ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, true);
                         }
                    );
                }

                @Test
                void validateParametersDoesNotThrow() throws ServiceBrokerException
                {
                    ParameterValidator.validateParameters(mockedServiceInstanceUpdateRequest, mockedPlan, true);
                }
            }
        }
    }
}
