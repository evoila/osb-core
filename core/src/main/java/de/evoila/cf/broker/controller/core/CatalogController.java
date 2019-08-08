package de.evoila.cf.broker.controller.core;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.model.catalog.Catalog;
import de.evoila.cf.broker.model.ApiVersions;
import de.evoila.cf.broker.model.annotations.ApiVersion;
import de.evoila.cf.broker.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Johannes Hiemer.
 */
@Controller
@RequestMapping(value = "/v2/catalog")
public class CatalogController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping(value = {"/", ""})
    @ApiVersion({ApiVersions.API_213, ApiVersions.API_214})
    public ResponseEntity<Catalog> getCatalog() {
        logger.debug("GET: getCatalog()");

        Catalog catalog = new Catalog();
        catalog.setServices(catalogService.getCatalog().getServices());

        return new ResponseEntity<>(catalog, HttpStatus.OK);
    }
}