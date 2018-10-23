package de.evoila.cf.broker.controller.custom;

import de.evoila.cf.broker.bean.ConditionOnBackupService;
import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.controller.utils.RestPageImpl;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.service.BackupService;
import de.evoila.cf.model.*;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/** @author Yannic Remmet. */
@RestController
@RequestMapping(value = "/custom/v2/manage/backup")
@Conditional(ConditionOnBackupService.class)
public class CustomBackupController extends BaseController {

    private BackupService backupService;

    public CustomBackupController(BackupService backupService) {
        Assert.notNull(backupService, "BackupService can not be null");
        this.backupService = backupService;
    }

    @GetMapping(value = "/{serviceInstanceId}/items")
    public ResponseEntity<Page<BackupItem>> items(@PathVariable String serviceInstanceId) {
        ResponseEntity<List<BackupItem>> response = backupService.getItems(serviceInstanceId);

        return new ResponseEntity<>(new PageImpl<>(response.getBody()), response.getStatusCode());
    }

    @PatchMapping(value = "/{serviceInstanceId}/backup/{planId}")
    public ResponseEntity<Object> backupNow(@PathVariable String serviceInstanceId,
                                            @PathVariable String planId, @RequestBody BackupRequest backupRequest)
            throws ServiceInstanceDoesNotExistException {
        ResponseEntity<Object> response = backupService.backupNow(planId, backupRequest);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PatchMapping(value = "/{serviceInstanceId}/restore/{planId}")
    public ResponseEntity<HashMap> restoreNow(@PathVariable String serviceInstanceId,
                                              @PathVariable String planId, @RequestBody RestoreRequest restoreRequest)
            throws ServiceInstanceDoesNotExistException {
        ResponseEntity<HashMap> response = backupService.restoreNow(planId, restoreRequest);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @GetMapping(value = "/{serviceInstanceId}/jobs")
    public ResponseEntity<RestPageImpl<BackupJob>> getJobs(@PathVariable String serviceInstanceId,
                                            @PageableDefault(size = 50) Pageable pageable) {
        ResponseEntity<RestPageImpl<BackupJob>> response = backupService.getJobs(serviceInstanceId, pageable);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @GetMapping(value = "/{serviceInstanceId}/jobs/{jobId}")
    public ResponseEntity<BackupJob> getJobs(@PathVariable String serviceInstanceId, @PathVariable String jobId) {
        ResponseEntity<BackupJob> response = backupService.getJob(jobId);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @DeleteMapping(value = "/{serviceInstanceId}/jobs/{jobId}")
    public ResponseEntity<BackupJob> deleteJobs(@PathVariable String serviceInstanceId, @PathVariable String jobId) {
        ResponseEntity<BackupJob> response = backupService.deleteJob(jobId);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    // PLANS
    @GetMapping(value = "/{serviceInstanceId}/plans")
    public ResponseEntity<Page<BackupPlan>> getPlans(@PathVariable String serviceInstanceId,
                                            @PageableDefault(size = 50) Pageable pageable) {
        ResponseEntity<RestPageImpl<BackupPlan>> response = backupService.getPlans(serviceInstanceId, pageable);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PostMapping(value = "/{serviceInstanceId}/plans")
    public ResponseEntity<BackupPlan> postPlan(@PathVariable String serviceInstanceId, @RequestBody BackupPlan plan)
            throws ServiceInstanceDoesNotExistException {
        ResponseEntity<BackupPlan> response = backupService.postPlan(serviceInstanceId, plan);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @GetMapping(value = "/{serviceInstanceId}/plans/{planId}")
    public ResponseEntity<BackupPlan> getPlan(@PathVariable String serviceInstanceId,
                                             @PathVariable String planId) {
        ResponseEntity<BackupPlan> response = backupService.getPlan(planId);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PatchMapping(value = "/{serviceInstanceId}/plans/{planId}")
    public ResponseEntity<BackupPlan> patchPlan(@PathVariable String serviceInstanceId,
                                             @PathVariable String planId,
                                             @RequestBody BackupPlan plan) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<BackupPlan> response = backupService.updatePlan(serviceInstanceId, planId, plan);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @DeleteMapping(value = "/{serviceInstanceId}/plans/{planId}")
    public ResponseEntity<BackupPlan> deletePlan(@PathVariable String serviceInstanceId,
                                              @PathVariable String planId) {
        ResponseEntity<BackupPlan> response = backupService.deletePlan(planId);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    // DESTINATIONS
    @GetMapping(value = "/{serviceInstanceId}/destinations")
    public ResponseEntity<RestPageImpl<FileDestination>> getDestinations(@PathVariable String serviceInstanceId,
                                                                @PageableDefault(size = 50) Pageable pageable) {
        ResponseEntity<RestPageImpl<FileDestination>> response = backupService.getDestinations(serviceInstanceId, pageable);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PostMapping(value = "/{serviceInstanceId}/destinations")
    public ResponseEntity<FileDestination> postDestination(@PathVariable String serviceInstanceId, @RequestBody FileDestination fileDestination) {
        ResponseEntity<FileDestination> response = backupService.postDestination(serviceInstanceId, fileDestination);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }


    @GetMapping(value = "/{serviceInstanceId}/destinations/{destinationId}")
    public ResponseEntity<FileDestination> getDestination(@PathVariable String serviceInstanceId,
                                           @PathVariable String destinationId)  {
        ResponseEntity<FileDestination> response = backupService.getDestination(destinationId);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PatchMapping(value = "/{serviceInstanceId}/destinations/{destinationId}")
    public ResponseEntity<FileDestination> putDestinaton(@PathVariable String serviceInstanceId,
                                             @PathVariable String destinationId,
                                             @RequestBody FileDestination fileDestination) throws ServiceInstanceDoesNotExistException {
        ResponseEntity<FileDestination> response = backupService.updateDestination(serviceInstanceId, destinationId, fileDestination);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @DeleteMapping(value = "/{serviceInstanceId}/destinations/{destinationId}")
    public ResponseEntity<FileDestination> deleteDestination(@PathVariable String serviceInstanceId,
                                              @PathVariable String destinationId) {
        ResponseEntity<FileDestination> response = backupService.deleteDestination(destinationId);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PostMapping(value = "/{serviceInstanceId}/destinations/validate")
    public ResponseEntity<FileDestination> validateDestination(@PathVariable String serviceInstanceId, @RequestBody FileDestination fileDestination) {
        ResponseEntity<FileDestination> response = backupService.validateDestination(serviceInstanceId, fileDestination);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

}