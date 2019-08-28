package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.CatalogIsNotValidException;
import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.catalog.MaintenanceInfo;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Service
@ConditionalOnBean(CatalogService.class)
@ConditionalOnProperty(prefix = "config.catalog", name = "validate", havingValue = "true")
@ConfigurationProperties(prefix = "config.catalog")
public class CatalogValidationService {

    public static String GUID_REGEX = "(\\{){0,1}[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}(\\}){0,1}";

    static Logger log = LoggerFactory.getLogger(CatalogValidationService.class);

    private boolean validate;
    private boolean strict;
    private boolean intrusive;

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

    public boolean isIntrusive() {
        return intrusive;
    }

    public void setIntrusive(boolean intrusive) {
        this.intrusive = intrusive;
    }

    public boolean isAllowedToChangeCatalog() {
        return !strict && intrusive;
    }

    /**
     * Method for Spring to run a validation on startup.
     * If the service is configured as {@linkplain #isStrict()}, the method will throw an uncaught {@linkplain CatalogIsNotValidException}.
     * The catalog will be provided by the {@linkplain CatalogService}.
     * For further information see {@linkplain #validateCatalog(Catalog)}.
     * @throws CatalogIsNotValidException
     */
    @PostConstruct
    public void validate() throws CatalogIsNotValidException {
        Catalog catalog = catalogService.getCatalog();
        if (!validateCatalog(catalog) && strict) {
            throw new CatalogIsNotValidException("The catalog that was build on the given configuration is not valid. " +
                    "Please see the logs of class 'CatalogValidationService' to identify flawed or missing fields.");
        }
    }

    /**
     * Runs a validation check on the given catalog. This includes following steps:
     * <ul>
     *     <li>null check</li>
     *     <li>has at least one service definition</li>
     *     <li>{@linkplain #validateServiceDefinition(ServiceDefinition)} for each service definition</li>
     * </ul>
     * If the service {@linkplain #isAllowedToChangeCatalog()}, then fields can be changed or parts disabled to prevent misconfiguration.
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
            if (isAllowedToChangeCatalog()) {
                log.info("Invalid parts were changed or disabled to ensure a proper start and runtime. " +
                        "Please review the catalog or logs of class 'CatalogValidationService' to see which parts were changed or disabled.");
            }
        }

        return valid;
    }

    /**
     * Runs a validation check on the given service definition. This includes following steps:
     * <ul>
     *     <li>null check</li>
     *     <li>{@linkplain #validateGuid(String)} for the id</li>
     *     <li>{@linkplain #validateServicePlan(Plan)} for each plan</li>
     * </ul>
     *
     * If the service {@linkplain #isAllowedToChangeCatalog()}, then fields can be changed or parts disabled to prevent misconfiguration.
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

        if (serviceDefinition.getPlans() == null) return true;

        // Foreach instead of Stream because of accessing a variable outside of the stream
        boolean valid = true;
        for (Plan plan : serviceDefinition.getPlans()) {
            valid &= validateServicePlan(plan);
        }
        return valid;
    }

    /**
     * Runs a validation check on the given service plan. This includes following steps:
     * <ul>
     *     <li>null check</li>
     *     <li>{@linkplain #validateGuid(String)} for the id</li>
     *     <li>{@linkplain #validateMaintenanceInfo(Plan)} for the maintenance info object</li>
     * </ul>
     * @param plan
     * @return
     */
    public boolean validateServicePlan(Plan plan) {
        if (plan == null) {
            log.info("A service definition contains a plan that is a null value");
            return false;
        }

        if (!validateGuid(plan.getId())) {
            log.info("Id of a plan is not a valid guid (name = " + plan.getName() + ")");
            return false;
        }

        return validateMaintenanceInfo(plan);
    }

    private boolean validateGuid(String guid) {
        return !StringUtils.isEmpty(guid) && guid.matches(GUID_REGEX);
    }

    private boolean validateMaintenanceInfo(Plan plan) {
        if (plan == null) {
            log.info("A plan is null.");
            return false;
        }

        if (plan.getMaintenanceInfo() == null) return true;

        MaintenanceInfo maintenanceInfo = plan.getMaintenanceInfo();

        if (StringUtils.isEmpty(maintenanceInfo.getVersion())) {
            logVersionFieldDoesNotExist(plan.getId());
            if (isAllowedToChangeCatalog()) {
                plan.setMaintenanceInfo(null);
            }
            return false;
        }

        if (!validateSemanticVersion2(maintenanceInfo.getVersion())){
            logIncorrectSemverVersion(plan.getId(), maintenanceInfo.getVersion());
            if (isAllowedToChangeCatalog()) {
                plan.setMaintenanceInfo(null);
            }
            return false;
        }
        return true;
    }

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

    private void logVersionFieldDoesNotExist(String planId) {
        log.info("Version field of maintenance_info for plan " + planId + " is not set, but necessary if the object exists.");
    }

    private boolean validateSemanticVersion2(String versionToCheck) {
        return !StringUtils.isEmpty(versionToCheck) &&
                versionToCheck.matches("^(0|[1-9]\\d*)" +
                        "\\.(0|[1-9]\\d*)" +
                        "\\.(0|[1-9]\\d*)" +
                        "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))" +
                        "?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");
    }
}
