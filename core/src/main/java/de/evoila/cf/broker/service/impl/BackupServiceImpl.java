package de.evoila.cf.broker.service.impl;

import de.evoila.cf.broker.bean.BackupConfiguration;
import de.evoila.cf.broker.bean.ConditionOnBackupService;
import de.evoila.cf.broker.controller.utils.RestPageImpl;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.service.BackupCustomService;
import de.evoila.cf.broker.service.BackupService;
import de.evoila.cf.config.security.AcceptSelfSignedClientHttpRequestFactory;
import de.evoila.cf.model.*;
import de.evoila.cf.model.enums.DestinationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Conditional(ConditionOnBackupService.class)
public class BackupServiceImpl implements BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupServiceImpl.class);

    private final RestTemplate restTemplate;

    private HttpHeaders headers;

    private BackupConfiguration backupConfiguration;

    private RabbitTemplate rabbitTemplate;

    private BackupCustomService backupCustomService;

    public BackupServiceImpl(BackupConfiguration backupConfiguration, BackupCustomService backupCustomService,
                             RabbitTemplate rabbitTemplate) {
        Assert.notNull(backupConfiguration, "BackupConfiguration can not be null");
        Assert.notNull(rabbitTemplate, "RabbitTemplate can not be null");

        this.backupConfiguration = backupConfiguration;
        this.backupCustomService = backupCustomService;
        this.restTemplate = new RestTemplate();
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    @ConditionalOnBean(AcceptSelfSignedClientHttpRequestFactory.class)
    @Autowired(required = false)
    private void selfSignedRestTemplate(AcceptSelfSignedClientHttpRequestFactory requestFactory) {
        restTemplate.setRequestFactory(requestFactory);
    }

    @PostConstruct
    private void backupEndpointHeaders() {
        this.headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        headers.add("Authorization", encodeCredentials());
    }

    private String encodeCredentials () {
        String str = backupConfiguration.getUser() + ":" + backupConfiguration.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(str.getBytes());
    }

    private UriComponentsBuilder buildUri(String path, Pageable pageable) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(backupConfiguration.getUri() + path);

        builder.queryParam("page", pageable.getPageNumber());
        builder.queryParam("page_size", pageable.getPageSize());
        if (pageable.getSort() != null) {
            Iterator<Sort.Order> sortIterator = pageable.getSort().iterator();
            while (sortIterator.hasNext()) {
                Sort.Order order = sortIterator.next();
                builder.queryParam("sort", order.getProperty() + "," + order.getDirection().toString());
            }
        }

        return builder;
    }

    @Override
    public ResponseEntity<List<BackupItem>> getItems(String serviceInstanceId) {
        List<BackupItem> backupItems = new ArrayList<>();
        try {
            for (Map.Entry<String, String> item : this.backupCustomService.getItems(serviceInstanceId).entrySet())
                backupItems.add(new BackupItem(item.getKey(), item.getValue()));

        } catch(Exception ex) {
            return new ResponseEntity("Could not load entitled items", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(backupItems, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> backupNow(String planId, BackupRequest backupRequest) {
        BackupPlan plan = this.getPlan(planId).getBody();
        backupRequest.setDestinationId(plan.getDestinationId());
        backupRequest.setPlan(plan);

        BackupConfiguration.Queue queue = this.backupConfiguration.getQueue();
        if (queue != null){
            rabbitTemplate.convertAndSend(queue.getExchange(), queue.getRoutingKey(), backupRequest);
        } else {
            String msg = "Backup RabbitMQ backupConfiguration is null. Please check configuration";
            logger.error(msg);
            return new ResponseEntity<>(msg,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new HashMap(),HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HashMap> restoreNow(String planId, RestoreRequest restoreRequest) {
        BackupPlan plan = this.getPlan(planId).getBody();
        restoreRequest.setPlan(plan);

        rabbitTemplate.convertAndSend(this.backupConfiguration.getQueue().getExchange(),
                                this.backupConfiguration.getQueue().getRoutingKey(), restoreRequest);

        return new ResponseEntity<>(new HashMap(), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<RestPageImpl<BackupJob>> getJobs(String serviceInstanceId, Pageable pageable) {
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("serviceInstanceId", serviceInstanceId);

        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<RestPageImpl<BackupJob>> response = restTemplate
                .exchange(buildUri("/jobs/byInstance/{serviceInstanceId}", pageable).buildAndExpand(uriParams).toUri(),
                    HttpMethod.GET, entity, new ParameterizedTypeReference<RestPageImpl<BackupJob>>() {});

        return response;
    }

    @Override
    public ResponseEntity<RestPageImpl<BackupPlan>> getPlans(String serviceInstanceId, Pageable pageable) {
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("serviceInstanceId", serviceInstanceId);

        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<RestPageImpl<BackupPlan>> response = restTemplate
                .exchange(buildUri("/plans/byInstance/{serviceInstanceId}", pageable).buildAndExpand(uriParams).toUri(),
                        HttpMethod.GET, entity, new ParameterizedTypeReference<RestPageImpl<BackupPlan>>() {});

        return response;
    }

    @Override
    public ResponseEntity<BackupJob> deleteJob(String jobId) {
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(backupConfiguration.getUri() + "/jobs/" + jobId,
                                                HttpMethod.DELETE, entity, BackupJob.class
        );
        return response;
    }

    @Override
    public ResponseEntity<BackupPlan> postPlan(String serviceInstanceId, BackupPlan plan) throws ServiceInstanceDoesNotExistException {
        EndpointCredential credentials = backupCustomService.getCredentials(serviceInstanceId);
        plan.setSource(credentials);
        plan.setServiceInstanceId(serviceInstanceId);
        HttpEntity entity = new HttpEntity(plan, headers);
        ResponseEntity response = restTemplate.exchange(backupConfiguration.getUri() + "/plans",
                                                HttpMethod.POST, entity, BackupPlan.class
        );
        return response;
    }

    @Override
    public ResponseEntity<BackupPlan> deletePlan(String planId) {
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(backupConfiguration.getUri() + "/plans/" + planId,
                                                HttpMethod.DELETE, entity, BackupPlan.class
        );
        return response;
    }

    @Override
    public ResponseEntity<BackupPlan> updatePlan(String serviceInstanceId, String planId, BackupPlan plan) throws ServiceInstanceDoesNotExistException {
        EndpointCredential credentials = backupCustomService.getCredentials(serviceInstanceId);
        plan.setSource(credentials);
        HttpEntity entity = new HttpEntity(plan, headers);
        ResponseEntity response = restTemplate.exchange(backupConfiguration.getUri() + "/plans/" + planId,
                                                HttpMethod.PUT, entity, BackupPlan.class
        );
        return response;
    }

    @Override
    public ResponseEntity<BackupJob> getJob(String jobId) {
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(backupConfiguration.getUri()+ "/jobs/" + jobId, HttpMethod.GET, entity, BackupJob.class);
    }

    @Override
    public ResponseEntity<BackupPlan> getPlan(String planId) {
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(backupConfiguration.getUri()+ "/plans/" + planId, HttpMethod.GET, entity, BackupPlan.class);
    }

    @Override
    public ResponseEntity<RestPageImpl<FileDestination>> getDestinations(String serviceInstanceId, Pageable pageable) {
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("serviceInstanceId", serviceInstanceId);

        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<RestPageImpl<FileDestination>> response = restTemplate
                                 .exchange(buildUri("/destinations/byInstance/{serviceInstanceId}", pageable)
                                 .buildAndExpand(uriParams).toUri(),
                                       HttpMethod.GET, entity, new ParameterizedTypeReference<RestPageImpl<FileDestination>>() {});
        return response;
    }

    @Override
    public ResponseEntity<FileDestination> getDestination(String destinationId) {
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(backupConfiguration.getUri()+ "/destinations/" + destinationId, HttpMethod.GET,
                entity, FileDestination.class);
    }

    @Override
    public ResponseEntity<FileDestination> postDestination(String serviceInstanceId, FileDestination fileDestination) {
        HttpEntity entity = new HttpEntity(fileDestination, headers);
        fileDestination.setInstanceId(serviceInstanceId);
        fileDestination.setType(DestinationType.SWIFT);
        ResponseEntity response = restTemplate.exchange(backupConfiguration.getUri() + "/destinations",
                                                HttpMethod.POST, entity, FileDestination.class
        );
        return response;
    }

    @Override
    public ResponseEntity<FileDestination> updateDestination(String serviceInstanceId, String destinationId, FileDestination fileDestination)  {
        HttpEntity entity = new HttpEntity(fileDestination, headers);
        fileDestination.setInstanceId(serviceInstanceId);
        fileDestination.setType(DestinationType.SWIFT);
        ResponseEntity response = restTemplate.exchange(backupConfiguration.getUri() + "/destinations/" + destinationId,
                                                HttpMethod.PUT, entity, FileDestination.class
        );
        return response;
    }

    @Override
    public ResponseEntity<FileDestination> deleteDestination(String destinationId) {
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity response = restTemplate.exchange(backupConfiguration.getUri() + "/destinations/" + destinationId,
                                                HttpMethod.DELETE, entity, FileDestination.class
        );
        return response;
    }

    @Override
    public ResponseEntity<FileDestination> validateDestination(String serviceInstanceId, FileDestination fileDestination) {
        HttpEntity entity = new HttpEntity(fileDestination, headers);
        fileDestination.setInstanceId(serviceInstanceId);
        ResponseEntity response = restTemplate.exchange(backupConfiguration.getUri() + "/destinations/validate",
                                                HttpMethod.POST, entity, FileDestination.class
        );
        return response;
    }

}
