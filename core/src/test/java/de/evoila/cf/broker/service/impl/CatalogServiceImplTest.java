package de.evoila.cf.broker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.Path;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    private static final Path resourcePath = Path.of(".",
                                                     "src",
                                                     "test",
                                                     "resources",
                                                     "CatalogServiceImpl");

    private static final String FILE_CATALOG_WITH_INACTIVE_PLANS            = "catalogSeveralServicesSeveralPlansWithSeveralInactiveOnes.json";
    private static final String FILE_CATALOG_WITH_INACTIVE_PLANS_FILTERED   = "catalogSeveralServicesSeveralPlansWithSeveralInactiveOnes-filtered.json";
    private static final String ID_EXPECTED_SERVICE_DEFINITION              = "8653E6CE-D760-4B63-98B8-7D6361BF1EB4";
    private static final String FILE_EXPECTED_SERVICE_DEFINITION            = "expectedServiceDefinition.json";

    @Mock
    private EndpointConfiguration endpointConfiguration;

    private Catalog catalog;
    private CatalogServiceImpl catalogService;

    private <T> T getObjectFromFile(Class<T> clazz, String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(resourcePath.resolve(filename).toFile(), clazz);
    }

    @BeforeEach
    void setUp() throws IOException {
        Environment environment = Mockito.mock(Environment.class);
        when(environment.getActiveProfiles()).thenReturn(new String[0]);

        catalog = getObjectFromFile(Catalog.class, FILE_CATALOG_WITH_INACTIVE_PLANS);
        catalogService = new CatalogServiceImpl(catalog, environment, endpointConfiguration, null);
    }

    void validateCatalog() throws IOException {
        assertSame(catalog, catalogService.getCatalog());
        assertEquals(getObjectFromFile(Catalog.class, FILE_CATALOG_WITH_INACTIVE_PLANS_FILTERED),
                     catalogService.getCatalog());
    }

    @Nested
    class getServiceDefinition {

        @Test
        void validId() throws IOException {
            ServiceDefinition expectedServiceDefinition = getObjectFromFile(ServiceDefinition.class, FILE_EXPECTED_SERVICE_DEFINITION);
            ServiceDefinition serviceDefinition = catalogService.getServiceDefinition(ID_EXPECTED_SERVICE_DEFINITION);
            assertEquals(expectedServiceDefinition, serviceDefinition);
            validateCatalog();
        }

        @Test
        void invalidId() throws IOException {
            ServiceDefinition serviceDefinition = catalogService.getServiceDefinition("576o");
            assertNull(serviceDefinition);
            validateCatalog();
        }

        @Test
        void nullId() throws IOException {
            ServiceDefinition serviceDefinition = catalogService.getServiceDefinition(null);
            assertNull(serviceDefinition);
            validateCatalog();
        }

    }

    @Nested
    class filterActivePlans {

        @Test
        void nullCatalog() throws IOException {
            catalogService.filterActivePlans(null);
            validateCatalog();
        }

        @Test
        void severalServicesWithSeveralPlansWithSeveralInactiveOnes() throws IOException {
            Catalog inputCatalog = getObjectFromFile(Catalog.class, FILE_CATALOG_WITH_INACTIVE_PLANS);
            catalogService.filterActivePlans(inputCatalog);
            assertEquals(getObjectFromFile(Catalog.class, FILE_CATALOG_WITH_INACTIVE_PLANS_FILTERED),
                         inputCatalog);
            validateCatalog();
        }

    }

}
