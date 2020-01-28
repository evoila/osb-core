package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.ResponseMessage;
import de.evoila.cf.broker.model.ServiceBrokerErrorResponse;
import de.evoila.cf.broker.util.EmptyRestResponse;
import org.everit.json.schema.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Johannes Hiemer, Marco Di Martino.
 **/
@Validated
public abstract class BaseController {

    private final Logger log = LoggerFactory.getLogger(BaseController.class);

    protected ResponseEntity processErrorResponse(HttpStatus status) {
        return new ResponseEntity(status);
    }

    protected ResponseEntity processEmptyErrorResponse(HttpStatus status) {
        return new ResponseEntity<>(EmptyRestResponse.BODY, status);
    }

    protected ResponseEntity processErrorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(new ResponseMessage<>(message), status);
    }

    protected ResponseEntity<ServiceBrokerErrorResponse> processErrorResponse(String error, String description, HttpStatus status) {
        log.debug("Handled following service broker error: " + error + " - " + description);
        return new ResponseEntity<>(new ServiceBrokerErrorResponse(error, description), status);
    }

    @ExceptionHandler({javax.validation.ValidationException.class})
    public ResponseEntity handleException(javax.validation.ValidationException ex) {
        return processErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity handleException(ValidationException ex) {
        return processErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MaintenanceInfoVersionsDontMatchException.class})
    public ResponseEntity handleException(MaintenanceInfoVersionsDontMatchException ex) {
        return processErrorResponse(ex.getError(), ex.getDescription(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({ConcurrencyErrorException.class})
    public ResponseEntity handleException(ConcurrencyErrorException ex) {
        return processErrorResponse(ex.getError(), ex.getDescription(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({AsyncRequiredException.class})
    public ResponseEntity handleException(AsyncRequiredException ex) {
        return processErrorResponse(ex.getError(), ex.getDescription(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ServiceBrokerFeatureIsNotSupportedException.class)
    public ResponseEntity handleException(ServiceBrokerFeatureIsNotSupportedException ex) {
        return processErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({ServiceDefinitionDoesNotExistException.class, ServiceDefinitionPlanDoesNotExistException.class, ServiceInstanceNotRetrievableException.class})
    public ResponseEntity handleException(Exception ex) {
        return processErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceInstanceExistsException.class)
    public ResponseEntity handleException(ServiceInstanceExistsException ex) {
        if (ex.isIdenticalInstance())
            return ResponseEntity.ok(ex.getResponse());
        return processErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ServiceInstanceDoesNotExistException.class)
    public ResponseEntity handleException(ServiceInstanceDoesNotExistException ex) {
        return processErrorResponse(HttpStatus.GONE);
    }

    @ExceptionHandler(ServiceInstanceNotFoundException.class)
    public ResponseEntity handleException(ServiceInstanceNotFoundException ex) {
        return processErrorResponse(ex.getError(), ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServiceInstanceBindingExistsException.class)
    public ResponseEntity handleException(ServiceInstanceBindingExistsException ex) {
        if (ex.isIdenticalBinding()) {
            return ResponseEntity.ok(ex.getResponse());
        }
        return processErrorResponse(HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ServiceInstanceBindingNotFoundException.class)
    public ResponseEntity handleException(ServiceInstanceBindingNotFoundException ex) {
        return processErrorResponse(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServiceInstanceBindingDoesNotExistsException.class)
    public ResponseEntity handleException(ServiceInstanceBindingDoesNotExistsException ex) {
        return processErrorResponse(HttpStatus.GONE);
    }

}
