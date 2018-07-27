package de.evoila.cf.broker.controller.custom;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.view.ServiceInstanceView;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author  Yannic Remmet & Johannes Hiemer, evoila.
 */
@Controller
@RequestMapping(value = "/custom/v2/manage")
public class CustomManageController extends BaseController {

    ServiceInstanceRepository repository;
    ServiceDefinitionRepository serviceDefinitionRepository;

    CustomManageController(ServiceInstanceRepository repository, ServiceDefinitionRepository sdRepository){
        Assert.notNull(repository, "ServiceInstanceRepository is null");
        Assert.notNull(sdRepository, "ServiceDefinitionRepository is null");
        this.repository = repository;
        this.serviceDefinitionRepository = sdRepository;
    }

    @GetMapping(value = "/{serviceInstanceId}")
    public ResponseEntity<ServiceInstanceView> getGeneralInformation(@PathVariable String serviceInstanceId) throws
            ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
        ServiceInstance serviceInstance = repository.getServiceInstance(serviceInstanceId);

        if(serviceInstance == null){
            throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
        }

        Plan plan = serviceDefinitionRepository.getPlan(serviceInstance.getPlanId());

        ServiceInstanceView serviceInstanceView = new ServiceInstanceView(serviceInstance, plan);

        return new ResponseEntity<>(serviceInstanceView, HttpStatus.OK);
    }


}
