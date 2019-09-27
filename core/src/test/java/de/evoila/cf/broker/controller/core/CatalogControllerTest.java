package de.evoila.cf.broker.controller.core;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileReader;
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

    @AfterEach
    void tearDown() {
    }

    /**
     * Reads a JSON file containing an array of ServiceDefinitions and returns a list containing all these service
     * definitions.
     * @param filePath  The path (relative or absolute) to the JSON file.
     * @return  A list containing all service definitions contained in the submitted file.
     * @throws Exception
     */
    private List<ServiceDefinition> readServicesFile(String filePath) throws Exception
    {
        List<ServiceDefinition> generatedServices;
        try (FileReader fileReader = new FileReader(new File(filePath))) {
            generatedServices = new Gson().fromJson(fileReader, new TypeToken<List<ServiceDefinition>>() {}.getType());
        }
        return generatedServices;
    }

    /**
     * A function testing CatalogController.getCatalog with a valid catalog.
     * The catalog is stored in a JSON file.
     * Not an @Test but called in @Test methods.
     * @param catalogPath           The path of the JSON file containing the catalog.
     * @param requestIdentity       A string representing a request identity.
     * @param originatingIdentity   A string representing a originating identity.
     * @throws Exception
     */
    private void validCatalogTest(String catalogPath,
                                  String requestIdentity,
                                  String originatingIdentity) throws Exception
    {
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
        assertEquals(expectedCatalog, response.getBody()); // ist dieser Test wirklich sinnvoll? Wir mocken ja gerade getServices. Wäre es ausreichend zu prüfen, dass der Catalog nicht null ist? Oder nicht leer? In diesen Fällen bräuchte man auch die ganzen equals Funktionen nicht hinzufügen.
    }

    @Test
    void getCatalog_EmptyCatalog() {
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
    void getCatalog_ValidCatalog()
            throws Exception {
        validCatalogTest(PATH_VALID_CATALOG_SERVICES,
                REQUEST_IDENTITY,
                ORIGINATING_IDENTITY);
    }

    @Test
    void getCatalog_ValidCatalogNoIdentityHeaders()
            throws Exception {
        validCatalogTest(PATH_VALID_CATALOG_SERVICES,
                null,
                null);
    }

    @Test
    void getCatalog_ValidCatalogInvalidIdentityFormat()
            throws Exception {
        validCatalogTest(PATH_VALID_CATALOG_SERVICES,
                ORIGINATING_IDENTITY, // expected: GUID, here: platform + base64-String
                REQUEST_IDENTITY);  // expected: platform + base64-String, here: GUID
    }

}
