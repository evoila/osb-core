package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** @author Johannes Hiemer. */
@RestController
@RequestMapping(value = "/v2/catalog")
public class CatalogController extends BaseController {
	
	private final Logger logger = LoggerFactory.getLogger(CatalogController.class);

	@Autowired 
	private CatalogService service;

	@GetMapping(value = { "/", "" })
	public @ResponseBody Catalog getCatalog() {
		logger.debug("GET: getCatalog()");
		return service.getCatalog();
	}
}
