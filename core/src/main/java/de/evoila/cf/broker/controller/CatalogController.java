package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.service.CatalogService;

import java.util.Arrays;
import java.util.stream.Collectors;

/** @author Johannes Hiemer. */
@Controller
@RequestMapping(value = "/v2/catalog")
public class CatalogController extends BaseController {

	private final Logger logger = LoggerFactory.getLogger(CatalogController.class);

	@Autowired
	private CatalogService service;

	@Autowired
    private Environment environment;

	@GetMapping(value = { "/", "" })
	public @ResponseBody Catalog getCatalog(){
		logger.debug("GET: getCatalog()");
		return prepareCatalogIfTesting(service.getCatalog());
	}

	private Catalog prepareCatalogIfTesting(Catalog catalog) {
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(
                env -> (env.equalsIgnoreCase("test")))) {

            catalog.getServices().stream().map(service -> {
                service.setId(replaceLastChar(service.getId()));

                service.getDashboardClient()
                        .setSecret(replaceLastChar(service.getDashboardClient().getSecret()));

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
}
