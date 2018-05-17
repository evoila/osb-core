package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * An implementation of the CatalogService that gets the catalog injected (ie
 * configure in spring config)
 *
 * @author sgreenberg@gopivotal.com
 * @author Christian Brinker, evoila.
 */
@Service
public class CatalogServiceImpl implements CatalogService {

	private final Logger logger = LoggerFactory.getLogger(CatalogServiceImpl.class);

	public static final String TEST_PROFILE = "test";

	private Catalog catalog;

	private Environment environment;

	private Map<String, ServiceDefinition> serviceDefs = new ConcurrentHashMap<String, ServiceDefinition>();

	@Autowired
	public CatalogServiceImpl(Catalog catalog, Environment environment) {
		this.catalog = catalog;
		this.environment = environment;
		prepareCatalogIfTesting(catalog);
		initializeMap();
	}

	private void initializeMap() {
		for (ServiceDefinition def : catalog.getServices()) {
			serviceDefs.put(def.getId(), def);
		}
	}

	@Override
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) {
		return serviceDefs.get(serviceId);
	}

	private Catalog prepareCatalogIfTesting(Catalog catalog) {
		if (Arrays.stream(environment.getActiveProfiles()).anyMatch(
				env -> (env.equalsIgnoreCase(TEST_PROFILE)))) {

			catalog.getServices().stream().map(service -> {
				if (service.getName().indexOf(TEST_PROFILE) == -1)
					service.setName(service.getName() + "-" + TEST_PROFILE);

				service.setId(replaceLastChar(service.getId()));
				service.getDashboardClient()
						.setSecret(replaceLastChar(service.getDashboardClient().getSecret()));


				if (service.getDashboardClient().getId().indexOf(TEST_PROFILE) == -1)
					service.getDashboardClient().setId(
							service.getDashboardClient().getId() + "-" + TEST_PROFILE
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

			if (url.getHost().indexOf(TEST_PROFILE) == -1) {
				URL newURL = new URL(url.getProtocol(),
						url.getHost().replaceFirst("\\.", "-" + TEST_PROFILE + "."),
						url.getPort(), url.getFile());
				urlStr = newURL.toString();
			}
		} catch(MalformedURLException ex) {
			logger.info("Exception replacing URL", ex);
		}

		return urlStr;
	}

}