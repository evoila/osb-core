package de.evoila.cf.broker.controller.custom;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.repository.BindingRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.BindingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @author Yannic Remmet.
 */
@RestController
@RequestMapping(value = "/custom/v2/manage/servicekeys")
public class CustomServiceKeysController extends BaseController {

    BindingRepository bindingRepository;
    BindingService bindingService;
    ServiceInstanceRepository serviceInstanceRepository;

    public CustomServiceKeysController(BindingRepository repository, BindingService service, ServiceInstanceRepository serviceInstanceRepository) {
        Assert.notNull(repository, "BindingRepository should not be null");
        Assert.notNull(service, "Binding Service should not be null");
        this.bindingRepository = repository;
        this.bindingService = service;
        this.serviceInstanceRepository = serviceInstanceRepository;
    }

    @GetMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<Page<ServiceInstanceBinding>> getGeneralInformation(
            @PathVariable String serviceInstanceId
    ) {
        List<ServiceInstanceBinding> bindings = bindingRepository.getBindingsForServiceInstance(serviceInstanceId);
        return new ResponseEntity<>(new PageImpl<>(bindings), HttpStatus.OK);
    }

    @GetMapping(value = "/{serviceInstanceId}/{serviceBindingId}")
    public ResponseEntity<ServiceInstanceBinding> getServiceKey(
            @PathVariable String serviceInstanceId, @PathVariable String serviceBindingId
    ) {
        ServiceInstanceBinding binding = bindingRepository.findOne(serviceBindingId);
        return new ResponseEntity<>(binding, HttpStatus.OK);
    }

    @PostMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<ServiceInstanceBinding> createServiceKey(
            @PathVariable String serviceInstanceId
    ) throws ServiceInstanceDoesNotExistException,
            ServiceBrokerException, ServiceInstanceBindingExistsException, ServiceDefinitionDoesNotExistException,
            ServiceBrokerFeatureIsNotSupportedException, PlatformException,
            InvalidParametersException, AsyncRequiredException {
        ServiceInstance instance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        if (instance == null) {
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }

        ServiceInstanceBindingRequest serviceInstanceBindingRequest = new ServiceInstanceBindingRequest(
                instance.getServiceDefinitionId(),
                instance.getPlanId()
        );

        String bindingId = UUID.randomUUID().toString();
        bindingService.createServiceInstanceBinding(bindingId, serviceInstanceId, serviceInstanceBindingRequest, false);
        ServiceInstanceBinding binding = bindingRepository.findOne(bindingId);
        return new ResponseEntity<>(binding, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{serviceInstanceId}/{serviceBindingId}")
    public ResponseEntity delete(@PathVariable String serviceInstanceId,
                                 @PathVariable String serviceBindingId)
            throws ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException,
            ServiceInstanceBindingDoesNotExistsException, AsyncRequiredException, ServiceBrokerException {

        ServiceInstance instance = serviceInstanceRepository.getServiceInstance(serviceInstanceId);

        if (instance == null) {
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }

        bindingService.deleteServiceInstanceBinding(serviceBindingId, instance.getPlanId(), false);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
