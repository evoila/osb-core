/**
 * 
 */
package de.evoila.cf.broker.controller.custom;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.controller.utils.DashboardAuthenticationRedirectBuilder;
import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.model.Dashboard;
import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.oauth.CompositeAccessToken;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.config.security.openid.OpenIdAuthenticationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URISyntaxException;

/** @author Johannes Hiemer. */
@Controller
@RequestMapping(value = "/custom/v2/authentication")
public class CustomAuthenticationController extends BaseController {

	private static final String CONFIRM = "/confirm";

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final static String REQUIRED_SCOPES = "cloud_controller_service_permissions.read openid";

	private static final String TOKEN_PREFIX = "Bearer ";

	private ServiceInstanceRepository serviceInstanceRepository;
	
	private CatalogService catalogService;

	private EndpointConfiguration endpointConfiguration;

	public CustomAuthenticationController(ServiceInstanceRepository serviceInstanceRepository, CatalogService catalogService,
                                          EndpointConfiguration endpointConfiguration) {
		Assert.notNull(serviceInstanceRepository, "ServiceInstance may not be null");
		Assert.notNull(catalogService, "CatalogService may not be null");
		Assert.notNull(endpointConfiguration, "GeneralConfigurationBean may not be null");

		this.serviceInstanceRepository = serviceInstanceRepository;
		this.catalogService = catalogService;
		this.endpointConfiguration = endpointConfiguration;
	}

    @GetMapping(value = "/{serviceInstanceId}")
    public Object authRedirect(@PathVariable String serviceInstanceId) throws URISyntaxException, IOException {
    	ServiceDefinition serviceDefinition = resolveServiceDefinitionByServiceInstanceId(serviceInstanceId);
    	if (serviceDefinition != null && serviceDefinition.getDashboard() != null
				&& serviceDefinition.getDashboard().getAuthEndpoint() != null
				&& DashboardUtils.isURL(serviceDefinition.getDashboard().getAuthEndpoint())) {
    			
    			Dashboard dashboard = serviceDefinition.getDashboard();
    			DashboardClient dashboardClient = serviceDefinition.getDashboardClient();

				String redirectUri =  DashboardUtils.redirectUri(dashboardClient, serviceInstanceId, CONFIRM);
    			DashboardAuthenticationRedirectBuilder dashboardAuthenticationRedirectBuilder 
    				= new DashboardAuthenticationRedirectBuilder(dashboard,
    						dashboardClient, redirectUri, REQUIRED_SCOPES);
    				
    			return new ModelAndView("redirect:" + dashboardAuthenticationRedirectBuilder.getRedirectUrl());
    	} else {
			return this.processErrorResponse("Service Definition of Service Instance could not be found",
					HttpStatus.NOT_FOUND);
		}
    }
    
    private ServiceDefinition resolveServiceDefinitionByServiceInstanceId(String serviceInstanceId) {
    	if (serviceInstanceRepository.containsServiceInstanceId(serviceInstanceId)) {
    		ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
    		
    		return catalogService.getServiceDefinition(serviceInstance.getServiceDefinitionId());
    	} else 
    		return null;
    }

	@GetMapping(value = "/{serviceInstanceId}" + CONFIRM)
    public Object confirm(@PathVariable String serviceInstanceId,
						  @RequestParam(value = "code") String authCode) throws Exception {
		ModelAndView mav = new ModelAndView("index");
		if (authCode == null)
			return this.processErrorResponse("No authentication code from UAA could be found",
					HttpStatus.UNAUTHORIZED);

		ServiceDefinition serviceDefinition = resolveServiceDefinitionByServiceInstanceId(serviceInstanceId);
		if (serviceDefinition != null) {
			Dashboard dashboard = serviceDefinition.getDashboard();
			DashboardClient dashboardClient = serviceDefinition.getDashboardClient();

			String redirectUri =  DashboardUtils.redirectUri(dashboardClient, serviceInstanceId, CONFIRM);

			CompositeAccessToken  token = OpenIdAuthenticationUtils
					.getAccessAndRefreshToken(dashboard.getAuthEndpoint(), authCode, dashboardClient, redirectUri);

			if (token != null) {
                mav.addObject("baseHref", "/core/authentication/" + serviceInstanceId);
				mav.addObject("token", TOKEN_PREFIX + token.getAccessToken());
				mav.addObject("serviceInstanceId", serviceInstanceId);
				mav.addObject("endpointUrl", endpointConfiguration.getDefault());
			} else {
				log.info("Did not receive a valid token, had to abort authentication...");
				return this.processErrorResponse("Token could not be processed/or is not valid",
						HttpStatus.UNAUTHORIZED);
			}
		} else
			return this.processErrorResponse("Service Definition of Service Instance could not be found",
					HttpStatus.UNAUTHORIZED);

		return mav;
	}

	/**
    @GetMapping(value = "/{serviceInstanceId}/test")
    public Object test(@PathVariable String serviceInstanceId) throws Exception {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("baseHref", "/core/authentication/" + serviceInstanceId + "/test");
        mav.addObject("token", TOKEN_PREFIX + "iojsiofksdfifid");
        mav.addObject("serviceInstanceId", serviceInstanceId);
        mav.addObject("endpointUrl", endpointConfiguration.getEndpointUrl());

        return mav;
    }
    **/

}
