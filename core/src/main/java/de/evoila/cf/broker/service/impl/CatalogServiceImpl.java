package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.interfaces.TransformCatalog;
import de.evoila.cf.broker.model.GlobalConstants;
import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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

	public CatalogServiceImpl(Catalog catalog, Environment environment, EndpointConfiguration endpointConfiguration, @Autowired(required = false ) List<TransformCatalog> transformCatalog) {
		this.catalog = catalog;
		this.environment = environment;
		this.endpointConfiguration = endpointConfiguration;

		filterActivePlans(catalog);
		if (transformCatalog != null) {
			transformCatalog.forEach(e -> e.transform(catalog, environment, endpointConfiguration));
			transformCatalog.forEach(e -> e.clean(catalog, environment, endpointConfiguration));
		}
		prepareCatalogIfTesting(catalog);
	}

	@Override
	public List<String> getServiceIdsWithoutHyphen(){
		return this.getCatalog().getServices().stream().map(serviceDefinition -> {
			return serviceDefinition.getId().replace("-","");
		}).collect(Collectors.toList());
	}

	@Override
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) throws ServiceDefinitionDoesNotExistException {

		return catalog.getServices().stream()
				.filter(serviceDefinition -> serviceDefinition.getId().equals(serviceId))
				.findFirst().orElseThrow(() -> new ServiceDefinitionDoesNotExistException(serviceId));
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
		if (catalog == null) {
			return;
		}
		catalog.getServices().forEach(serviceDefinition ->
		{
			serviceDefinition.setPlans(
					serviceDefinition.getPlans().stream().filter(plan ->
					{
						return plan.getMetadata().isActive();
					}).collect(Collectors.toList()));
		});
	}

}
