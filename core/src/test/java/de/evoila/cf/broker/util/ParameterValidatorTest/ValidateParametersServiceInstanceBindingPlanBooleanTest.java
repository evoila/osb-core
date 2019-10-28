package de.evoila.cf.broker.util.ParameterValidatorTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
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

public class ValidateParametersServiceInstanceBindingPlanBooleanTest extends BaseTest
{
    @Mock
    private Plan                          mockedPlan;
    @Mock
    private Schemas                       mockedSchemas;
    @Mock
    private SchemaServiceBinding          mockedServiceBinding;
    @Mock
    private ServiceInstanceBindingRequest mockedServiceInstanceBindingRequest;

    @BeforeEach
    void setUpValidateParameters()
    {
        readJsonSchema();
        initializeInputMap();
    }

    @Nested
    class isUpdateFalse
    {
        @Test
        void serviceInstanceBindingRequestNull()
        {
            assertThrows(IllegalArgumentException.class, () -> {
                     ParameterValidator.validateParameters((ServiceInstanceBindingRequest) null, mockedPlan, false);
                 }
            );
        }

        @Test
        void planNull() throws ServiceBrokerException
        {
            ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, null, false);
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

                    ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, false);
                }

                @Test
                void getServiceInstanceReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceBinding())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, false);
                }

                @Test
                void getCreateReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceBinding())
                            .thenReturn(mockedServiceBinding);
                    when(mockedServiceBinding.getCreate())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, false);
                }

                @Test
                void getParametersReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceBinding())
                            .thenReturn(mockedServiceBinding);
                    when(mockedServiceBinding.getCreate())
                            .thenReturn(mockedServiceCreate);
                    when(mockedServiceCreate.getParameters())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, false);
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
                    when(mockedSchemas.getServiceBinding())
                            .thenReturn(mockedServiceBinding);
                    when(mockedServiceBinding.getCreate())
                            .thenReturn(mockedServiceCreate);
                    when(mockedServiceCreate.getParameters())
                            .thenReturn(schema);
                }

                @Test
                void serviceInstanceBindingRequestParametersNull()
                {
                    when(mockedServiceInstanceBindingRequest.getParameters() )
                            .thenReturn(null);

                    ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                                                                        ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, false);
                                                                    }
                                                                   );
                    assertSame(JSONException.class, exception.getCause().getClass());
                }

                @Test
                void validateParametersThrowsServiceBrokerException() throws JsonProcessingException
                {
                    JSONException mockedException = new JSONException("Test"){};
                    doThrow(mockedException)
                            .when(mockedObjectMapper)
                            .writeValueAsString(schema);

                    ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                                                                        ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, false);
                                                                    }
                                                                   );
                    assertSame(mockedException, exception.getCause());
                }

                @Test
                void validateParametersThrowsValidationException() throws JsonProcessingException
                {
                    when(mockedServiceInstanceBindingRequest.getParameters())
                            .thenReturn(inputMap);
                    doReturn(schemaString)
                            .when(mockedObjectMapper)
                            .writeValueAsString(schema);
                    doReturn(invalidJsonString)
                            .when(mockedObjectMapper)
                            .writeValueAsString(inputMap);

                    assertThrows(ValidationException.class, () -> {
                             ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, false);
                         }
                    );
                }

                @Test
                void validateParametersDoesNotThrow() throws ServiceBrokerException
                {
                    when(mockedServiceInstanceBindingRequest.getParameters())
                            .thenReturn(inputMap);
                    
                    ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, false);
                }
            }
        }
    }

    @Nested
    class isUpdateTrue
    {
        @Test
        void serviceInstanceBindingRequestNull()
        {
            assertThrows(IllegalArgumentException.class, () -> {
                     ParameterValidator.validateParameters((ServiceInstanceBindingRequest) null, mockedPlan, true);
                 }
            );
        }

        @Test
        void planNull() throws ServiceBrokerException
        {
            ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, null, true);
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

                    ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, true);
                }

                @Test
                void getServiceInstanceReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceBinding())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, true);
                }

                @Test
                void getCreateReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceBinding())
                            .thenReturn(mockedServiceBinding);
                    when(mockedServiceBinding.getUpdate())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, true);
                }

                @Test
                void getParametersReturnsNull() throws ServiceBrokerException
                {
                    when(mockedPlan.getSchemas())
                            .thenReturn(mockedSchemas);
                    when(mockedSchemas.getServiceBinding())
                            .thenReturn(mockedServiceBinding);
                    when(mockedServiceBinding.getUpdate())
                            .thenReturn(mockedServiceUpdate);
                    when(mockedServiceUpdate.getParameters())
                            .thenReturn(null);

                    ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, true);
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
                    when(mockedSchemas.getServiceBinding())
                            .thenReturn(mockedServiceBinding);
                    when(mockedServiceBinding.getUpdate())
                            .thenReturn(mockedServiceUpdate);
                    when(mockedServiceUpdate.getParameters())
                            .thenReturn(schema);
                }

                @Test
                void serviceInstanceBindingRequestParametersNull()
                {
                    when(mockedServiceInstanceBindingRequest.getParameters() )
                            .thenReturn(null);

                    ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                                                                        ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, true);
                                                                    }
                                                                   );
                    assertSame(JSONException.class, exception.getCause().getClass());
                }

                @Test
                void validateParametersThrowsServiceBrokerException() throws JsonProcessingException
                {
                    JSONException mockedException = new JSONException("Test"){};
                    doThrow(mockedException)
                            .when(mockedObjectMapper)
                            .writeValueAsString(schema);

                    ServiceBrokerException exception = assertThrows(ServiceBrokerException.class, () -> {
                                                                        ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, true);
                                                                    }
                                                                   );
                    assertSame(mockedException, exception.getCause());
                }

                @Test
                void validateParametersThrowsValidationException() throws JsonProcessingException
                {
                    when(mockedServiceInstanceBindingRequest.getParameters())
                            .thenReturn(inputMap);
                    doReturn(schemaString)
                            .when(mockedObjectMapper)
                            .writeValueAsString(schema);
                    doReturn(invalidJsonString)
                            .when(mockedObjectMapper)
                            .writeValueAsString(inputMap);

                    assertThrows(ValidationException.class, () -> {
                            ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, true);
                        }
                    );
                }

                @Test
                void validateParametersDoesNotThrow() throws ServiceBrokerException
                {
                    when(mockedServiceInstanceBindingRequest.getParameters())
                            .thenReturn(inputMap);

                    ParameterValidator.validateParameters(mockedServiceInstanceBindingRequest, mockedPlan, true);
                }
            }
        }
    }
}
