package de.evoila.cf.broker.controller.custom;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.DeploymentService;
import org.everit.json.schema.ValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.Map;

/**
 * @author Yannic Remmet, Johannes Hiemer.
 */
@Controller
@RequestMapping(value = "/custom/v2/manage/service_instances")
public class CustomManageController extends BaseController {

    ServiceInstanceRepository serviceInstanceRepository;

    ServiceDefinitionRepository serviceDefinitionRepository;

    DeploymentService deploymentService;

    CustomManageController(ServiceInstanceRepository serviceInstanceRepository,
                           ServiceDefinitionRepository serviceDefinitionRepository,
                           DeploymentService deploymentService) {
        Assert.notNull(serviceInstanceRepository, "ServiceInstanceRepository is null");
        Assert.notNull(serviceDefinitionRepository, "ServiceDefinitionRepository is null");
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.serviceDefinitionRepository = serviceDefinitionRepository;
        this.deploymentService = deploymentService;
    }

    @GetMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<ServiceInstance> get(@PathVariable String serviceInstanceId) throws
            ServiceBrokerException, ServiceInstanceNotFoundException,
            ServiceInstanceDoesNotExistException, ConcurrencyErrorException {

        ServiceInstance serviceInstance = deploymentService.fetchServiceInstance(serviceInstanceId);

        if (serviceInstance == null) {
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }

        return new ResponseEntity<>(serviceInstance, HttpStatus.OK);
    }

    @PatchMapping(value = "/{serviceInstanceId}")
    public ResponseEntity submit(@PathVariable("serviceInstanceId") String serviceInstanceId,
                                 @RequestBody Map<String, Object> request
    ) throws ServiceBrokerException,
            ServiceInstanceDoesNotExistException, ValidationException {

        ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);
        if (serviceInstance == null)
            throw new ServiceInstanceDoesNotExistException("Could not find Service Instance");

        try {
            ServiceInstanceUpdateRequest serviceInstanceRequest = new ServiceInstanceUpdateRequest(serviceInstance.getServiceDefinitionId(),
                    serviceInstance.getPlanId(), null);
            serviceInstanceRequest.setParameters(request);

            deploymentService.updateServiceInstance(serviceInstanceId, serviceInstanceRequest);
        } catch (ServiceDefinitionDoesNotExistException e) {
            return new ResponseEntity<>(new ResponseMessage<>(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        UriComponents uriComponents = MvcUriComponentsBuilder
                .fromMethodCall(MvcUriComponentsBuilder.on(CustomManageController.class)
                        .lastOperation(serviceInstanceId, null)).build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, uriComponents.toUriString());

        return new ResponseEntity<>(new ResponseMessage<>("Configuration updated successfully"), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/{serviceInstanceId}/last_operation")
    public ResponseEntity<JobProgressResponse> lastOperation(
            @PathVariable("serviceInstanceId") String serviceInstanceId,
            @RequestParam(value = "operation", required = false) String operation)
            throws ServiceInstanceDoesNotExistException {

        JobProgressResponse jobProgressResponse;
        if (operation != null)
            jobProgressResponse = deploymentService.getLastOperationById(serviceInstanceId, operation);
        else
            jobProgressResponse = deploymentService.getLastOperationByReferenceId(serviceInstanceId);

        return new ResponseEntity<>(jobProgressResponse, HttpStatus.OK);
    }

}
