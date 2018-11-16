package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.util.EnvironmentUtils;
import de.evoila.cf.broker.util.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * An implementation of the CatalogService that gets the catalog injected (ie
 * configure in spring config)
 *
 * @author Johannes Hiemer.
 * @author Christian Brinker, evoila.
 */
@Service
public class CatalogServiceImpl implements CatalogService {

    private final Logger logger = LoggerFactory.getLogger(CatalogServiceImpl.class);

	private Catalog catalog;

	private Environment environment;

	private EndpointConfiguration endpointConfiguration;

	public CatalogServiceImpl(Catalog catalog, Environment environment, EndpointConfiguration endpointConfiguration) {
		this.catalog = catalog;
		this.environment = environment;
		this.endpointConfiguration = endpointConfiguration;
		prepareCatalogIfTesting(catalog);
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
	    boolean isTestEnvironment = EnvironmentUtils.isTestEnvironment(environment);
		if (isTestEnvironment) {

			catalog.getServices().stream().map(service -> {
				if (service.getName().indexOf(GlobalConstants.TEST_PROFILE) == -1)
					service.setName(service.getName() + "-" + GlobalConstants.TEST_PROFILE);

				service.setId(EnvironmentUtils.replaceLastChar(service.getId()));
				service.getDashboardClient()
						.setSecret(EnvironmentUtils.replaceLastChar(service.getDashboardClient().getSecret()));


				if (service.getDashboardClient().getId().indexOf(GlobalConstants.TEST_PROFILE) == -1)
					service.getDashboardClient().setId(
							service.getDashboardClient().getId() + "-" + GlobalConstants.TEST_PROFILE
					);

				service.getDashboard().setUrl(
                        EnvironmentUtils.replaceUrl(service.getDashboard().getUrl())
				);
				service.getDashboardClient().setRedirectUri(
                        EnvironmentUtils.replaceUrl(service.getDashboardClient().getRedirectUri())
				);

				service.getPlans().stream().map(plan -> {
					plan.setId(EnvironmentUtils.replaceLastChar(plan.getId()));

					return plan;
				}).collect(Collectors.toList());
				return service;
			}).collect(Collectors.toList());

			endpointConfiguration.setDefault(
                    EnvironmentUtils.replaceUrl(endpointConfiguration.getDefault())
            );

			endpointConfiguration.getCustom().forEach(s -> {
			    s.setUrl(EnvironmentUtils.replaceUrl(s.getUrl()));
            });
		}
		return catalog;
	}
}
