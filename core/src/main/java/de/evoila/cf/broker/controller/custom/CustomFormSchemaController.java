package de.evoila.cf.broker.controller.custom;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.ServiceDefinition;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.json.schema.JsonSchema;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yannic Remmet, Johannes Hiemer.
 **/
@RestController
@RequestMapping(value = "/custom/v2/manage/formSchema")
public class CustomFormSchemaController extends BaseController {

    private ServiceInstanceRepository serviceInstanceRepository;

    private CatalogService catalogService;

    public CustomFormSchemaController(ServiceInstanceRepository serviceInstanceRepository,
                                      CatalogService catalogService) {
        Assert.notNull(catalogService, "CatalogService can not be null");
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.catalogService = catalogService;
    }

    @GetMapping(value = "/{serviceInstanceId}/update")
    public ResponseEntity<Map> items(@PathVariable String serviceInstanceId) throws ServiceInstanceDoesNotExistException {

        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
        if (serviceInstance == null)
            throw new ServiceInstanceDoesNotExistException("Could not find Service Instance");

        ServiceDefinition serviceDefinition = catalogService.getServiceDefinition(serviceInstance.getServiceDefinitionId());

        Plan plan = serviceDefinition.getPlans().stream()
                .filter(p -> p.getId().equals(serviceInstance.getPlanId()))
                .findFirst().orElse(null);

        if (plan != null && plan.getSchemas() != null && plan.getSchemas().getServiceInstance() != null
                && plan.getSchemas().getServiceInstance().getUpdate() != null) {

            JsonSchema schemaParameters = plan.getSchemas()
                    .getServiceInstance().getUpdate().getParameters();

            Map response = Collections.unmodifiableMap(new HashMap<String, JsonSchema>() {{
                put("schema", schemaParameters);
            }});

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}