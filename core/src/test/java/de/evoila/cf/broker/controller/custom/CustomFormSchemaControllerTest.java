package de.evoila.cf.broker.controller.custom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.catalog.plan.SchemaServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.SchemaServiceUpdate;
import de.evoila.cf.broker.model.catalog.plan.Schemas;
import de.evoila.cf.broker.model.json.schema.JsonSchema;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.CatalogService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomFormSchemaControllerTest {

    private static final String HAPPY_SERVICE_INSTANCE_ID   = "800e96eb-3bd2-4142-911e-cf3730d942b5";
    private static final String HAPPY_SERVICE_DEFINITION_ID = "fef09fbc-c120-4868-812b-ab7e264e8d09";
    private static final String HAPPY_PLAN_ID               = "39d40d0f-c0f7-4c84-9875-16da2d93b8c2";

    @Mock
    private ServiceInstanceRepository serviceInstanceRepository;
    @Mock
    private CatalogService catalogService;

    @Mock
    private ServiceInstance serviceInstance;
    @Mock
    private ServiceDefinition serviceDefinition;
    @Mock
    private Plan plan;
    @Mock
    private Schemas schemas;
    @Mock
    private SchemaServiceInstance schemaServiceInstance;
    @Mock
    private SchemaServiceUpdate schemaServiceUpdate;

    private CustomFormSchemaController controller;

    @BeforeEach
    void setUp() {
        controller = new CustomFormSchemaController(serviceInstanceRepository, catalogService);
    }

    @Nested
    class exceptionThrown {

        @Test
        void withGetServiceInstanceThrowing() throws ServiceInstanceDoesNotExistException {
            ServiceInstanceDoesNotExistException expectedEx = new ServiceInstanceDoesNotExistException("Mock");
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenThrow(expectedEx);
            ServiceInstanceDoesNotExistException ex = assertThrows(ServiceInstanceDoesNotExistException.class,
                                                                   () -> controller.items(HAPPY_SERVICE_INSTANCE_ID));
            assertSame(expectedEx, ex);
        }

        @Test
        void withGetServiceDefinitionThrowing() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
            ServiceDefinitionDoesNotExistException expectedEx = new ServiceDefinitionDoesNotExistException("Mock");
            when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                    .thenReturn(serviceInstance);
            when(serviceInstance.getServiceDefinitionId())
                    .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
            when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                    .thenThrow(expectedEx);
            ServiceDefinitionDoesNotExistException ex = assertThrows(ServiceDefinitionDoesNotExistException.class,
                                                                     () -> controller.items(HAPPY_SERVICE_INSTANCE_ID));
            assertSame(expectedEx, ex);
        }

    }

    private void mocksForNoException() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
        when(serviceInstanceRepository.getServiceInstance(HAPPY_SERVICE_INSTANCE_ID))
                .thenReturn(serviceInstance);
        when(serviceInstance.getServiceDefinitionId())
                .thenReturn(HAPPY_SERVICE_DEFINITION_ID);
        when(catalogService.getServiceDefinition(HAPPY_SERVICE_DEFINITION_ID))
                .thenReturn(serviceDefinition);
    }

    @Nested
    class notFoundResponse {

        @BeforeEach
        void setUp() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
            mocksForNoException();
        }

        private void validateResponse(ResponseEntity<Map> response) {
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Nested
        class withPlanNull {

            @Test
            void withEmptyPlansList() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
                when(serviceDefinition.getPlans())
                        .thenReturn(new ArrayList<>());
                ResponseEntity<Map> response = controller.items(HAPPY_SERVICE_INSTANCE_ID);
                validateResponse(response);
            }

            @Test
            void withNoMatchingPlanId() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
                when(serviceDefinition.getPlans())
                        .thenReturn(new ArrayList<>() {{
                            add(new Plan("ID1",
                                         "NAME1",
                                         "DESCRIPTION1",
                                         Platform.EXISTING_SERVICE,
                                         true));
                            add(new Plan("ID2",
                                         "NAME2",
                                         "DESCRIPTION2",
                                         Platform.EXISTING_SERVICE,
                                         true));
                        }});
                when(serviceInstance.getPlanId())
                        .thenReturn(HAPPY_PLAN_ID);
                ResponseEntity<Map> response = controller.items(HAPPY_SERVICE_INSTANCE_ID);
                validateResponse(response);
            }

        }

        private void mocksForValidPlan() {
            when(serviceDefinition.getPlans())
                    .thenReturn(new ArrayList<>() {{
                        add(new Plan("ID1",
                                     "NAME1",
                                     "DESCRIPTION1",
                                     Platform.EXISTING_SERVICE,
                                     true));
                        add(plan);
                    }});
            when(serviceInstance.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
            when(plan.getId())
                    .thenReturn(HAPPY_PLAN_ID);
        }

        @Test
        void withSchemasNull() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
            mocksForValidPlan();
            when(plan.getSchemas())
                    .thenReturn(null);
            ResponseEntity<Map> response = controller.items(HAPPY_SERVICE_INSTANCE_ID);
            validateResponse(response);
        }

        @Test
        void withSchemaServiceInstanceNull() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
            mocksForValidPlan();
            when(plan.getSchemas())
                    .thenReturn(schemas);
            when(schemas.getServiceInstance())
                    .thenReturn(null);
            ResponseEntity<Map> response = controller.items(HAPPY_SERVICE_INSTANCE_ID);
            validateResponse(response);
        }

        @Test
        void withSchemaUpdateNull() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
            mocksForValidPlan();
            when(plan.getSchemas())
                    .thenReturn(schemas);
            when(schemas.getServiceInstance())
                    .thenReturn(schemaServiceInstance);
            when(schemaServiceInstance.getUpdate())
                    .thenReturn(null);
            ResponseEntity<Map> response = controller.items(HAPPY_SERVICE_INSTANCE_ID);
            validateResponse(response);
        }

    }

    @Nested
    class okResponse {

        @BeforeEach
        void setUp() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
            mocksForNoException();
            when(serviceDefinition.getPlans())
                    .thenReturn(new ArrayList<>() {{
                        add(new Plan("ID1",
                                     "NAME1",
                                     "DESCRIPTION1",
                                     Platform.EXISTING_SERVICE,
                                     true));
                        add(plan);
                    }});
            when(serviceInstance.getPlanId())
                    .thenReturn(HAPPY_PLAN_ID);
            when(plan.getId())
                    .thenReturn(HAPPY_PLAN_ID);
            when(plan.getSchemas())
                    .thenReturn(schemas);
            when(schemas.getServiceInstance())
                    .thenReturn(schemaServiceInstance);
            when(schemaServiceInstance.getUpdate())
                    .thenReturn(schemaServiceUpdate);
        }

        private void validateResponse(Map expectedBody, ResponseEntity<Map> response) {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedBody, response.getBody());
        }

        @Test
        void withNullSchemaParameters() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
            when(schemaServiceUpdate.getParameters())
                    .thenReturn(null);
            ResponseEntity<Map> response = controller.items(HAPPY_SERVICE_INSTANCE_ID);
            validateResponse(new HashMap<>() {{
                put("schema", null);
            }}, response);

        }

        @Test
        void withNotNullSchemaParameters() throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
            JsonSchema expectedJsonSchema = mock(JsonSchema.class);
            when(schemaServiceUpdate.getParameters())
                    .thenReturn(expectedJsonSchema);
            ResponseEntity<Map> response = controller.items(HAPPY_SERVICE_INSTANCE_ID);
            validateResponse(new HashMap<>() {{
                put("schema", expectedJsonSchema);
            }}, response);
        }

    }

}
