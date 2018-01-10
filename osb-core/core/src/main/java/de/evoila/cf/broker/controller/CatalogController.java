package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.exception.BadHeaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.service.CatalogService;

import javax.servlet.http.HttpServletResponse;

/** @author Johannes Hiemer. */
@Controller
@RequestMapping(value = "/v2/catalog")
public class CatalogController extends BaseController {
	
	private final Logger logger = LoggerFactory.getLogger(CatalogController.class);

	@Autowired 
	private CatalogService service;
	
	@RequestMapping(value = { "/", "" }, method = RequestMethod.GET)
	public @ResponseBody Catalog getCatalog(){
		logger.debug("GET: getCatalog()");
		/*if (headers.containsKey(getHeader())) {
			if (!(headers.get(getHeader()).get(0).equals(getVersion()))) {
				throw new BadHeaderException(headers.get(getHeader()).get(0));
			}
		}else{
				throw new BadHeaderException(null);
			}
		*/
		return service.getCatalog();
	}
}
