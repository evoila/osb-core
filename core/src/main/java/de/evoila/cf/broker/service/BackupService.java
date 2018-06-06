package de.evoila.cf.broker.service;

import de.evoila.cf.broker.controller.utils.RestPageImpl;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

public interface BackupService {

    ResponseEntity<Object> backupNow(String serviceInstanceId, BackupRequest fileDestination) throws ServiceInstanceDoesNotExistException;

    ResponseEntity<HashMap> restoreNow(String serviceInstanceId, RestoreRequest fileDestination) throws ServiceInstanceDoesNotExistException;

    ResponseEntity<RestPageImpl<BackupJob>> getJobs(String serviceInstanceId, Pageable pageable);

    ResponseEntity<RestPageImpl<BackupPlan>> getPlans(String serviceInstanceId, Pageable pageable);

    ResponseEntity<BackupJob> deleteJob(String jobId);

    ResponseEntity<BackupPlan> postPlan(String serviceInstanceId, BackupPlan plan) throws ServiceInstanceDoesNotExistException;

    ResponseEntity<BackupPlan> deletePlan(String planId);

    ResponseEntity<BackupPlan> updatePlan(String serviceInstanceId, String planId, BackupPlan plan) throws ServiceInstanceDoesNotExistException;

    ResponseEntity<BackupJob> getJob(String jobId);

    ResponseEntity<BackupPlan> getPlan(String planId);

    ResponseEntity<RestPageImpl<FileDestination>> getDestinations(String serviceInstanceId, Pageable pageable);

    ResponseEntity<FileDestination> getDestination(String destinationId);

    ResponseEntity<FileDestination> postDestination(String serviceInstanceId, FileDestination fileDestination);

    ResponseEntity<FileDestination> updateDestination(String serviceInstanceId, String destinationId, FileDestination fileDestination) throws ServiceInstanceDoesNotExistException;

    ResponseEntity<FileDestination> deleteDestination(String destinationId);

    ResponseEntity<FileDestination> validateDestination(String serviceInstanceId, FileDestination fileDestination);
    
    ResponseEntity<List<BackupItem>> getItems(String serviceInstanceId);
}
