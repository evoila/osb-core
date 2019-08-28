package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.interfaces.TranformCatalog;
import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.catalog.MaintenanceInfo;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.model.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An implementation of the CatalogService that gets the catalog injected (ie
 * configure in spring config)
 *
 * @author Johannes Hiemer, Christian Brinker, evoila.
 */
@Service
public class CatalogServiceImpl implements CatalogService {

	private final Logger logger = LoggerFactory.getLogger(CatalogServiceImpl.class);

	private Catalog catalog;

	private Environment environment;

	private EndpointConfiguration endpointConfiguration;

	public CatalogServiceImpl(Catalog catalog, Environment environment, EndpointConfiguration endpointConfiguration, @Autowired(required = false ) List<TranformCatalog> tranformCatalog) {
		this.catalog = catalog;
		this.environment = environment;
		this.endpointConfiguration = endpointConfiguration;


		filterActivePlans(catalog);
		if (tranformCatalog != null) {
			tranformCatalog.forEach(e -> e.tranform(catalog, environment, endpointConfiguration));
			tranformCatalog.forEach(e -> e.clean(catalog, environment, endpointConfiguration));
		}
		prepareCatalogIfTesting(catalog);

		controlAndManageAllMaintenanceInfos(catalog);
	}

	@Override
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) {
		return catalog.getServices().stream()
                .filter(serviceDefinition -> {
                    if (serviceDefinition.getId().equals(serviceId))
                        return true;
                    else
                        return false;
                }).findFirst().orElse(null);
	}

	private Catalog prepareCatalogIfTesting(Catalog catalog) {
		if (Arrays.stream(environment.getActiveProfiles()).anyMatch(
				env -> (env.equalsIgnoreCase(GlobalConstants.TEST_PROFILE)))) {

			catalog.getServices().stream().map(service -> {
				if (!service.getName().contains(GlobalConstants.TEST_PROFILE))
					service.setName(service.getName() + "-" + GlobalConstants.TEST_PROFILE);

				service.setId(replaceLastChar(service.getId()));
				service.getDashboardClient()
						.setSecret(replaceLastChar(service.getDashboardClient().getSecret()));


				if (!service.getDashboardClient().getId().contains(GlobalConstants.TEST_PROFILE))
					service.getDashboardClient().setId(
							service.getDashboardClient().getId() + "-" + GlobalConstants.TEST_PROFILE
					);

				service.getDashboard().setUrl(
						replaceUrl(service.getDashboard().getUrl())
				);
				service.getDashboardClient().setRedirectUri(
						replaceUrl(service.getDashboardClient().getRedirectUri())
				);

				service.getPlans().stream().map(plan -> {
					plan.setId(replaceLastChar(plan.getId()));

					return plan;
				}).collect(Collectors.toList());
				return service;
			}).collect(Collectors.toList());

			endpointConfiguration.setDefault(
			        replaceUrl(endpointConfiguration.getDefault())
            );

			endpointConfiguration.getCustom().forEach(s -> {
			    s.setUrl(replaceUrl(s.getUrl()));
            });
		}
		return catalog;
	}

	private String replaceLastChar(String value) {
		if (value != null && value.length() > 1)
			return value.substring(0, value.length() - 1).concat("T");

		return value;
	}

	private String replaceUrl(String urlStr) {
		try {
			URL url = new URL(urlStr);

			if (!url.getHost().contains(GlobalConstants.TEST_PROFILE)) {
				URL newURL = new URL(url.getProtocol(),
						url.getHost().replaceFirst("\\.", "-" + GlobalConstants.TEST_PROFILE + "."),
						url.getPort(), url.getFile());
				urlStr = newURL.toString();
			}
		} catch(MalformedURLException ex) {
			logger.info("Exception replacing URL", ex);
		}

		return urlStr;
	}

	public void filterActivePlans(Catalog catalog) {
		catalog.getServices().stream().forEach(serviceDefinition ->
		{
			serviceDefinition.setPlans(
					serviceDefinition.getPlans().stream().filter(plan ->
					{
						return plan.getMetadata().isActive();
					}).collect(Collectors.toList()));
		});
	}

	private void controlAndManageAllMaintenanceInfos(Catalog catalog) {
		Optional.ofNullable(catalog).ifPresent(o -> {
			catalog.getServices().forEach(serviceDefinition -> {
						serviceDefinition.getPlans().forEach(this::controlAndManageSingleMaintenanceInfo);
					}
			);
		});
	}

	private void controlAndManageSingleMaintenanceInfo(Plan plan) {
		Optional.ofNullable(plan.getMaintenanceInfo()).ifPresent(maintenanceInfo -> {
			String version = maintenanceInfo.getVersion();

			if (StringUtils.isEmpty(version)) {
				logVersionFieldDoesNotExist(plan.getId());
				plan.setMaintenanceInfo(null);
			} else{
				validateAndLogSemverVersion(plan);
				}
		});
	}

	private void validateAndLogSemverVersion(Plan plan){
		MaintenanceInfo maintenanceInfo = plan.getMaintenanceInfo();
		if(!checkIfVersionIsSemantic2(maintenanceInfo.getVersion())){
			plan.setMaintenanceInfo(null);
			logIncorrectSemverVersion(plan.getId(), maintenanceInfo.getVersion());
		}
	}

	private void logIncorrectSemverVersion(String planId, String version){
		logger.error("The configured version of the maintenance_info for plan " +  planId
				+ "is not complying with required Semantic Versioning 2.0.0. Disabling it to prevent false configuration.");
		logger.info("\n######################"
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
		logger.error("Version field of maintenance_info for plan " + planId +
				" is not set, but necessary if the object exists. Disabling it to prevent false configuration.");
	}

	private boolean checkIfVersionIsSemantic2(String versionToCheck) {
		return !StringUtils.isEmpty(versionToCheck) &&
			versionToCheck.matches("^(0|[1-9]\\d*)" +
					"\\.(0|[1-9]\\d*)" +
					"\\.(0|[1-9]\\d*)" +
					"(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))" +
					"?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");
	}

}
