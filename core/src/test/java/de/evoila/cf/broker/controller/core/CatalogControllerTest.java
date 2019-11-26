package de.evoila.cf.broker.controller.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.service.CatalogService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

    private static final String PATH_VALID_CATALOG_SERVICES = "." + File.separator +
                                                              "src" + File.separator +
                                                              "test" + File.separator +
                                                              "resources" + File.separator +
                                                              "catalogServices.json";
    private static final String REQUEST_IDENTITY = "a82715af-3dcd-4650-bb90-dad096294e16";
    private static final String ORIGINATING_IDENTITY = "cloudfoundry eyANCiAgInVzZXJfaWQiOiAiNjgzZWE3NDgtMzA5Mi00ZmY0LWI2NTYtMzljYWNjNGQ1MzYwIg0KfQ==";

    @Mock
    private Catalog catalog;
    @Mock
    private CatalogService catalogService;
    private CatalogController catalogController;

    @BeforeEach
    void setUp() {
        this.catalogController = new CatalogController(catalogService);
    }

    /**
     * Reads a JSON file containing an array of ServiceDefinitions and returns a list containing all these service
     * definitions.
     *
     * @param filePath  The path (relative or absolute) to the JSON file.
     *
     * @return  A list containing all service definitions contained in the submitted file.
     *
     * @throws IOException  Cannot read JSON file.
     */
    private List<ServiceDefinition> readServicesFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), new TypeReference<List<ServiceDefinition>>() {});
    }

    /**
     * A function testing CatalogController.getCatalog with a valid catalog.
     * The catalog is stored in a JSON file.
     * Not an @Test but called in @Test methods.
     *
     * @param catalogPath           The path of the JSON file containing the catalog.
     * @param requestIdentity       A string representing a request identity.
     * @param originatingIdentity   A string representing a originating identity.
     *
     * @throws IOException  {@link CatalogControllerTest#readServicesFile(String)} throws
     */
    private void validCatalogTest(String catalogPath,
                                  String requestIdentity,
                                  String originatingIdentity)
            throws IOException {
        // mocks
        when(catalogService.getCatalog()).thenReturn(catalog);

        List<ServiceDefinition> generatedServices = readServicesFile(catalogPath);
        when(catalog.getServices()).thenReturn(generatedServices);
        // actual method call
        ResponseEntity<Catalog> response = catalogController.getCatalog(requestIdentity,
                originatingIdentity);
        // create expected object
        Catalog expectedCatalog = new Catalog();
        List<ServiceDefinition> expectedServices = readServicesFile(catalogPath);
        expectedCatalog.setServices(expectedServices);
        // check results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCatalog, response.getBody());
    }

    @Nested
    class getCatalog {

        @Test
        void emptyCatalog() {
            // mocks
            when(catalogService.getCatalog()).thenReturn(catalog);
            when(catalog.getServices()).thenReturn(new ArrayList<>());
            // actual method call
            ResponseEntity<Catalog> response = catalogController.getCatalog(REQUEST_IDENTITY,
                                                                            ORIGINATING_IDENTITY);
            // check results
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(new Catalog(), response.getBody());
        }

        @Test
        void validCatalog() throws IOException {
            validCatalogTest(PATH_VALID_CATALOG_SERVICES,
                             REQUEST_IDENTITY,
                             ORIGINATING_IDENTITY);
        }

    }

}
