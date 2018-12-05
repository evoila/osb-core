package de.evoila.cf.broker.controller.custom;

import de.evoila.cf.broker.bean.ConditionOnBackupService;
import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.backup.BackupItem;
import de.evoila.cf.broker.model.backup.CreateItem;
import de.evoila.cf.broker.service.BackupCustomService;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** @author Yannic Remmet. */
@RestController
@RequestMapping(value = "/custom/v2/manage/backup")
@Conditional(ConditionOnBackupService.class)
public class CustomBackupController extends BaseController {

    private BackupCustomService backupCustomService;

    public CustomBackupController(BackupCustomService backupCustomService) {
        Assert.notNull(backupCustomService, "BackupService can not be null");
        this.backupCustomService = backupCustomService;
    }

    @GetMapping(value = "/{serviceInstanceId}/items")
    public ResponseEntity<Page<BackupItem>> items(@PathVariable String serviceInstanceId, @PageableDefault(size = 10,sort = {"name"},
            direction = Sort.Direction.DESC) Pageable pageable) throws ServiceDefinitionDoesNotExistException,
            ServiceBrokerException, ServiceInstanceDoesNotExistException {
        Map<String, String> responses = backupCustomService.getItems(serviceInstanceId);

        List<BackupItem> backupItems = responses.entrySet().stream()
                .map(x -> new BackupItem(x.getKey(), x.getValue()))
                .collect(Collectors.toList());

        return new ResponseEntity(new PageImpl<>(backupItems), HttpStatus.OK);
    }

    @PostMapping(value = "/{serviceInstanceId}/items")
    public ResponseEntity<CreateItem> item(@PathVariable String serviceInstanceId, @RequestBody CreateItem createItem) {
        try {
            backupCustomService.createItem(serviceInstanceId, createItem.getName(), createItem.getParameters());
        } catch (Exception ex) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(createItem, HttpStatus.CREATED);
    }

}