package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.CatalogIsNotValidException;
import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.catalog.MaintenanceInfo;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@ConditionalOnBean(CatalogService.class)
@ConditionalOnProperty(prefix = "config.catalog", name = "validate", havingValue = "true")
@ConfigurationProperties(prefix = "config.catalog")
public class CatalogValidationService {

    public static String GUID_REGEX = "(\\{){0,1}[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}(\\}){0,1}";

    static Logger log = LoggerFactory.getLogger(CatalogValidationService.class);

    private boolean validate;
    private boolean strict;

    private CatalogService catalogService;

    public CatalogValidationService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * Method for Spring to run a validation on startup.
     * If the service is configured as {@linkplain #isStrict()}, the method will throw an uncaught {@linkplain CatalogIsNotValidException}.
     * The catalog will be provided by the {@linkplain CatalogService}.
     * For further information see {@linkplain #validateCatalog(Catalog)}.
     * @throws CatalogIsNotValidException if catalog does not pass the validation check and the validation service {@linkplain #isStrict()}
     */
    @PostConstruct
    public void validate() throws CatalogIsNotValidException {
        Catalog catalog = catalogService.getCatalog();
        if (!validateCatalog(catalog) && strict) {
            throw new CatalogIsNotValidException("""
                    The catalog that was build on the given configuration is not valid. \
                    Please see the logs of class 'CatalogValidationService' to identify flawed or missing fields.\
                    """);
        }
    }

    /**
     * Runs a validation check on the given catalog. This includes following steps:
     * <ul>
     *     <li>null check</li>
     *     <li>has at least one service definition</li>
     *     <li>{@linkplain #validateServiceDefinition(ServiceDefinition)} for each service definition</li>
     * </ul>
     * @param catalog Catalog object to validate
     * @return true if the catalog is valid according to the performed checks or false if at least one check fails
     */
    public boolean validateCatalog(Catalog catalog) {
        if (catalog == null || catalog.getServices() == null || catalog.getServices().size() == 0) {
            log.info("Catalog is null or the catalog holds no services.");
            return false;
        }

        boolean valid = true;
        for (ServiceDefinition serviceDefinition : catalog.getServices()) {
            valid &= validateServiceDefinition(serviceDefinition);
        }
        if (!valid) {
            log.info("The catalog that was build on the given configuration is not valid.");
        } else {
            log.info("Catalog was validated and passed.");
        }

        return valid;
    }

    /**
     * Runs a validation check on the given service definition. This includes following steps:
     * <ul>
     *     <li>null check</li>
     *     <li>{@linkplain #validateGuid(String)} for the id</li>
     *     <li>{@linkplain #validateServicePlan(String, Plan)} for each plan</li>
     * </ul>
     *
     * @param serviceDefinition ServiceDefinition object to validate
     * @return true if the service definition is valid according to the performed checks or false if at least one check fails
     */
    public boolean validateServiceDefinition(ServiceDefinition serviceDefinition) {
        if (serviceDefinition == null) {
            log.info("The catalog contains a service definition that is a null value");
            return false;
        }

        if (!validateGuid(serviceDefinition.getId())) {
            log.info("Id of a service definition is not a valid guid (name = " + serviceDefinition.getName() + ")");
            return false;
        }
        if (ObjectUtils.isEmpty(serviceDefinition.getName())) {
            log.info("Name of service definition " + serviceDefinition.getId() + " is null or empty.");
            return false;
        }
        if (ObjectUtils.isEmpty(serviceDefinition.getDescription())) {
            log.info("Description of service definition " + serviceDefinition.getId() + " is null or empty.");
            return false;
        }

        if (serviceDefinition.getPlans() == null || serviceDefinition.getPlans().isEmpty()) {
            log.info("Service definition " + serviceDefinition.getId() + " has no plans.");
            return false;
        }

        // Foreach instead of Stream because of accessing a variable outside of the stream
        boolean valid = true;
        for (Plan plan : serviceDefinition.getPlans()) {
            valid &= validateServicePlan(plan.getId(), plan);
        }
        return valid;
    }

    /**
     * Runs a validation check on the given service plan. This includes following steps:
     * <ul>
     *     <li>null check</li>
     *     <li>{@linkplain #validateGuid(String)} for the id</li>
     *     <li>{@linkplain #validateMaintenanceInfo(String, MaintenanceInfo)} for the maintenance info object</li>
     * </ul>
     * @param serviceDefinitionId id of the owning service definition used for logging purposes
     * @param plan the plan object to validate
     * @return
     */
    public boolean validateServicePlan(String serviceDefinitionId, Plan plan) {
        if (plan == null) {
            log.info("The service definition " + serviceDefinitionId + " contains a plan that is a null value");
            return false;
        }

        if (!validateGuid(plan.getId())) {
            log.info("Id of a plan of the service definition " + serviceDefinitionId + " is not a valid guid (name = " + plan.getName() + ")");
            return false;
        }
        if (ObjectUtils.isEmpty(plan.getName())) {
            log.info("Name of plan " + plan.getId() + " is null or empty.");
            return false;
        }
        if (ObjectUtils.isEmpty(plan.getDescription())) {
            log.info("Description of plan " + plan.getId() + " is null or empty.");
            return false;
        }

        return validateMaintenanceInfo(plan.getId(), plan.getMaintenanceInfo());
    }

    /**
     * Validates a given String to be a GUID.
     * @param guid string to validate
     * @return true if the given string qualifies for a GUID and false if it does not
     */
    private boolean validateGuid(String guid) {
        return !ObjectUtils.isEmpty(guid) && guid.matches(GUID_REGEX);
    }

    /**
     * Validates a given maintenance_info object by checking its version for existence
     * and compliance with Semantic Version 2
     * @param planId id of the owning plan for logging purposes
     * @param maintenanceInfo maintenance_info object to validate
     * @return true if the given maintenance_info is valid or false if it is not
     */
    private boolean validateMaintenanceInfo(String planId, MaintenanceInfo maintenanceInfo) {
        if (maintenanceInfo == null) return true;

        if (ObjectUtils.isEmpty(maintenanceInfo.getVersion())) {
            logVersionFieldDoesNotExist(planId);
            return false;
        }

        if (!validateSemanticVersion2(maintenanceInfo.getVersion())){
            logIncorrectSemverVersion(planId, maintenanceInfo.getVersion());
            return false;
        }
        return true;
    }

    /**
     * Logs the incorrect version with the given id of the plan object owning maintenance_info object.
     * Also logs a few examples of Semantic Versioning 2 to help the user with changing the version.
     * @param planId id of the plan object, that owns the maintenance_info object
     * @param version incorrect version string
     */
    private void logIncorrectSemverVersion(String planId, String version){
        log.info("\n######################"
                + "\n The version of the maintenance_info object for plan " + planId + " is not complying to required Semantic Versioning 2.0.0"
                + "\n The version should look like following examples:"
                + "\n- 1.2.3 "
                + "\n- 2.0.0"
                + "\n- 2.2.1-rc.1"
                + "\n- 1.0.0-beta"
                + "\nPlease change your current value '" + version + "' to a compatible string."
                + "\n######################");
    }

    /**
     * Logs the absence of the {@linkplain MaintenanceInfo#getVersion()} field.
     * @param planId id of the plan object, that owns the maintenance_info object
     */
    private void logVersionFieldDoesNotExist(String planId) {
        log.info("Version field of maintenance_info for plan " + planId + " is not set, but necessary if the object exists.");
    }

    /**
     * Validates a String to qualify as a Semantic Versioning 2 version.
     * @param versionToCheck the version String to check
     * @return true if given version String is Semantic Versioning 2
     */
    private boolean validateSemanticVersion2(String versionToCheck) {
        return !ObjectUtils.isEmpty(versionToCheck) &&
                versionToCheck.matches("^(0|[1-9]\\d*)" +
                        "\\.(0|[1-9]\\d*)" +
                        "\\.(0|[1-9]\\d*)" +
                        "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))" +
                        "?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");
    }
}
