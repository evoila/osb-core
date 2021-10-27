package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.CatalogIsNotValidException;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.catalog.MaintenanceInfo;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Metadata;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.UUID;


import static org.junit.Assert.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class CatalogValidationServiceTest {

    private Catalog catalog;
    private CatalogValidationService catalogValidationService;

    /**
     * Initializes the catalog and validation service object of this test.
     * Creates one service offering with three service plans.
     * The resulting test is supposed to be valid.
     */
    @Before
    public void initCatalog() {

        Plan planA = getTestPlan(
                UUID.randomUUID().toString(),
                "test_plan_A",
                "This plan A is a valid test plan at creation.",
                Platform.EXISTING_SERVICE,
                true,
                "1.0.0-beta"
        );
        Plan planB = getTestPlan(
                UUID.randomUUID().toString(),
                "test_plan_B",
                "This plan B is a valid test plan at creation.",
                Platform.BOSH,
                false,
                "2.4.1-rc.1"
        );
        Plan planC = getTestPlan(
                UUID.randomUUID().toString(),
                "test_plan_C",
                "This plan C is a valid test plan at creation.",
                Platform.EXISTING_SERVICE,
                false,
                "3.12.1"
        );

        ServiceDefinition serviceDefinition = new ServiceDefinition(
                UUID.randomUUID().toString(),
                "valid_test_service_definition",
                "This is a valid test service definition.",
                true,
                Arrays.asList(planA, planB, planC),
                true
        );

        catalog = new Catalog();
        catalog.setServices(Collections.singletonList(serviceDefinition));

        catalogValidationService = new CatalogValidationService(null);
        catalogValidationService.setStrict(true);
        catalogValidationService.setValidate(true);
    }

    private Plan getTestPlan(String id, String name, String description, Platform platform, boolean free, String maintenanceVersion) {
        Plan plan = new Plan(id, name, description, platform, free);
        Metadata metadata = new Metadata();
        plan.setMetadata(metadata);
        metadata.setBullets(Arrays.asList("testing_" + name, "test_" + name , "no real value"));
        metadata.setDisplayName(name + "_displayname");
        metadata.setActive(true);
        plan.setMaintenanceInfo(new MaintenanceInfo(maintenanceVersion, "First beta release"));

        return plan;
    }

    /**
     * Returns a specific plan from the catalog object of this test.
     * Be aware that no checks for index position are done and exceptions can occur if handled incorrectly.
     * See {@linkplain #initCatalog()} and {@linkplain #getTestPlan(String, String, String, Platform, boolean, String)} for information about the creation of the service plan.
     * @param index index of the plan to get
     * @return service plan object that corresponds to the given index
     */
    private Plan getPlan(int index) {
        return catalog.getServices().get(0).getPlans().get(index);
    }

    /**
     * Returns the service definition object of this tests catalog object.
     * See {@linkplain #initCatalog()} for information about the creation of the service definition.
     * @return the service definition of this tests catalog object
     */
    private ServiceDefinition getServiceDefinition() {
        return catalog.getServices().get(0);
    }

    /**
     * Executes assertion commands for the catalog, the service definition and plans A, B and C.
     * When expecting a negative validation, this method only takes one plan into consideration when changed.
     * @param expected whether to expect a valid or invalid catalog
     * @param nameOfChangedObject name of the object that was changed for logging purposes
     * @param indexOfChangedPlan indicates which plan was made invalid; field is ignored if a valid catalog is expected
     */
    private void assertCatalogComponents(boolean expected, String nameOfChangedObject, int indexOfChangedPlan) {
        if (expected) {
            assertTrue("Default tested catalog should be valid but was not.",
                    catalogValidationService.validateCatalog(catalog));

            assertTrue("Default tested service definition should be valid but was not.",
                    catalogValidationService.validateServiceDefinition(getServiceDefinition()));


            getServiceDefinition().getPlans().forEach(plan -> {
                assertTrue("Default tested plan "+ plan.getName() + " should be valid but is not.",
                        catalogValidationService.validateServicePlan(getServiceDefinition().getId(), plan));
            });
        } else {
            assertFalse("Catalog should be invalid after changes to " + nameOfChangedObject + " but is not.",
                    catalogValidationService.validateCatalog(catalog));
            assertFalse("Service definition should be invalid after changes to " + nameOfChangedObject + " but is not.",
                    catalogValidationService.validateServiceDefinition(getServiceDefinition()));

            if (getServiceDefinition().getPlans() == null || getServiceDefinition().getPlans().isEmpty()) {
                return;
            }
                
            if (indexOfChangedPlan == 0) {
                assertFalse("Plan A should be invalid after changes to " + nameOfChangedObject + " but is not.",
                        catalogValidationService.validateServicePlan(getServiceDefinition().getId(), getPlan(0)));
            } else {
                assertTrue("Plan A should still be valid after changes to " + nameOfChangedObject + " but is not.",
                        catalogValidationService.validateServicePlan(getServiceDefinition().getId(), getPlan(0)));
            }

            if (indexOfChangedPlan == 1) {
                assertFalse("Plan B should be invalid after changes to " + nameOfChangedObject + " but is not.",
                        catalogValidationService.validateServicePlan(getServiceDefinition().getId(), getPlan(1)));
            } else {
                assertTrue("Plan B should still be valid after changes to " + nameOfChangedObject + " but is not.",
                        catalogValidationService.validateServicePlan(getServiceDefinition().getId(), getPlan(1)));
            }

            if (indexOfChangedPlan == 2) {
                assertFalse("Plan C should be invalid after changes to " + nameOfChangedObject + " but is not.",
                        catalogValidationService.validateServicePlan(getServiceDefinition().getId(), getPlan(2)));
            } else {
                assertTrue("Plan C should still be valid after changes to " + nameOfChangedObject + " but is not.",
                        catalogValidationService.validateServicePlan(getServiceDefinition().getId(), getPlan(2)));
            }
        }
    }

    /**
     * Tests the tests catalog object with {@linkplain #assertCatalogComponents(boolean, String, int)} and expects a valid catalog.
     * The catalog has to be created and initiated beforehand.
     */
    @Test
    public void testValidDefault() {
        assertCatalogComponents(true, "", -1);
    }

    @Test
    public void testInvalidPlans() {
        // Test non guid id
        Plan planInvalid = getPlan(0);
        planInvalid.setId("nonsense####");
        assertCatalogComponents(false, "plan A", 0);

        // Test name equals null
        initCatalog();
        planInvalid = getPlan(1);
        planInvalid.setName(null);
        assertCatalogComponents(false, "plan B", 1);

        // Test empty name
        initCatalog();
        planInvalid = getPlan(2);
        planInvalid.setName("");
        assertCatalogComponents(false, "plan C", 2);

        // Test description equals null
        initCatalog();
        planInvalid = getPlan(0);
        planInvalid.setDescription(null);
        assertCatalogComponents(false, "plan A", 0);

        // Test empty description
        initCatalog();
        planInvalid = getPlan(1);
        planInvalid.setDescription("");
        assertCatalogComponents(false, "plan B", 1);

        // Test maintenance_info version being not semantic version 2
        initCatalog();
        planInvalid = getPlan(2);
        MaintenanceInfo maintenanceInfo = planInvalid.getMaintenanceInfo();
        maintenanceInfo.setVersion("nonsense#again");
        assertCatalogComponents(false, "plan C's maintenance_info", 2);
    }

    @Test
    public void testInvalidServiceDefinition() {
        // Test non guid id
        ServiceDefinition definitionInvalid = getServiceDefinition();
        definitionInvalid.setId("nonsense####");
        assertCatalogComponents(false, "service definition", -1);

        // Test name equals null
        initCatalog();
        definitionInvalid = getServiceDefinition();
        definitionInvalid.setName(null);
        assertCatalogComponents(false, "service definition", -1);

        // Test empty name
        initCatalog();
        definitionInvalid = getServiceDefinition();
        definitionInvalid.setName("");
        assertCatalogComponents(false, "service definition", -1);

        // Test description equals null
        initCatalog();
        definitionInvalid = getServiceDefinition();
        definitionInvalid.setDescription(null);
        assertCatalogComponents(false, "service definition", -1);

        // Test empty description
        initCatalog();
        definitionInvalid = getServiceDefinition();
        definitionInvalid.setDescription("");
        assertCatalogComponents(false, "service definition", -1);

        // Test empty plan list
        initCatalog();
        definitionInvalid = getServiceDefinition();
        definitionInvalid.setPlans(new LinkedList<>());
        assertCatalogComponents(false, "service definition's list of plans", -1);

        // Test plans list equals null
        initCatalog();
        definitionInvalid = getServiceDefinition();
        definitionInvalid.setPlans(null);
        assertCatalogComponents(false, "service definition's list of plans", -1);
    }

    @Test
    public void testInvalidCatalog() {
        // Test services equals null
        catalog.setServices(null);
        assertFalse("Catalog has null for services list but passed validation.", catalogValidationService.validateCatalog(catalog));

        // Test empty services list
        initCatalog();
        catalog.setServices(new LinkedList<>());
        assertFalse("Catalog has no service definitions but passed validation.", catalogValidationService.validateCatalog(catalog));
    }

    @Test(expected = CatalogIsNotValidException.class)
    public void testValidationExceptionThrown() throws CatalogIsNotValidException {
        CatalogService catalogService = new CatalogService() {
            @Override
            public Catalog getCatalog() {
                Catalog catalog = new Catalog();
                catalog.setServices(null);
                return catalog;
            }

            @Override
            public ServiceDefinition getServiceDefinition(String serviceId) {
                return null;
            }
        };
        catalogValidationService = new CatalogValidationService(catalogService);
        catalogValidationService.setValidate(true);
        catalogValidationService.setStrict(true);

        catalogValidationService.validate();
    }
}
