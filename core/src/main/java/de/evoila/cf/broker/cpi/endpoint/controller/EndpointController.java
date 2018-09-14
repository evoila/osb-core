package de.evoila.cf.broker.cpi.endpoint.controller;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.cpi.endpoint.EndpointAvailabilityService;
import de.evoila.cf.broker.model.cpi.EndpointServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@Controller
@RequestMapping(value = "/core/endpoint")
public class EndpointController extends BaseController {
	
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(EndpointController.class);

	private EndpointAvailabilityService endpointAvailabilityService;

	public EndpointController(EndpointAvailabilityService endpointAvailabilityService) {
		this.endpointAvailabilityService = endpointAvailabilityService;
	}
	
	@GetMapping(value = { "/", "" })
	public @ResponseBody Map<String, EndpointServiceState> getCatalog() {
		return endpointAvailabilityService.getServices();
	}
	
}
