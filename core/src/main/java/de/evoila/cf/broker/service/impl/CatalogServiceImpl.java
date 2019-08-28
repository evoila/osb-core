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

		controllAndManageMaintenanceInfo(catalog);
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
				if (service.getName().indexOf(GlobalConstants.TEST_PROFILE) == -1)
					service.setName(service.getName() + "-" + GlobalConstants.TEST_PROFILE);

				service.setId(replaceLastChar(service.getId()));
				service.getDashboardClient()
						.setSecret(replaceLastChar(service.getDashboardClient().getSecret()));


				if (service.getDashboardClient().getId().indexOf(GlobalConstants.TEST_PROFILE) == -1)
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

			if (url.getHost().indexOf(GlobalConstants.TEST_PROFILE) == -1) {
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

	public void controllAndManageMaintenanceInfo(Catalog catalog) {
		if (catalog == null || catalog.getServices() == null) return;
		for (ServiceDefinition definition : catalog.getServices()) {
			if (definition.getPlans() != null) {
				for (Plan plan : definition.getPlans()) {
					MaintenanceInfo maintenanceInfo = plan.getMaintenanceInfo();
					if (maintenanceInfo != null) {
						String version = maintenanceInfo.getVersion();
						if (StringUtils.isEmpty(version)) {
							logger.error("Version field of maintenance_info for plan "
									+ (plan.getId() == null ? "'ID NOT SET'" : plan.getId())
									+ " is not set, but necessary if the object exists. Disabling it to prevent false configuration.");
							plan.setMaintenanceInfo(null);
						} else if (!checkIfVersionIsSemantic2(version)) {
							logger.error("The configured version of the maintenance_info for plan "
									+ (plan.getId() == null ? "'ID NOT SET'" : plan.getId())
									+ "is not complying with required Semantic Versioning 2.0.0. Disabling it to prevent false configuration.");
							plan.setMaintenanceInfo(null);
							logger.info("######################");
							logger.info("The version of the maintenance_info object for plan "+ (plan.getId() == null ? "'ID NOT SET'" : plan.getId()) + " is not complying to required Semantic Versioning 2.0.0");
							logger.info("The version should look like following examples:");
							logger.info("- 1.2.3");
							logger.info("- 2.0.0");
							logger.info("- 2.2.1-rc.1");
							logger.info("- 1.0.0-beta");
							logger.info("Please change your current value '"+version+"' to a compatible string.");
							logger.info("######################");
						}
					}
				}
			}
		}
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
