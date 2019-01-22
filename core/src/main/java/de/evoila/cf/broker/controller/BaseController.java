package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.exception.*;
import de.evoila.cf.broker.model.ResponseMessage;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Johannes Hiemer, Marco Di Martino.
 **/
public abstract class BaseController {

	private final Logger log = LoggerFactory.getLogger(BaseController.class);

    protected ResponseEntity processErrorResponse(HttpStatus status) {
        return new ResponseEntity(status);
    }

	protected ResponseEntity processErrorResponse(String message, HttpStatus status) {
		return new ResponseEntity(new ResponseMessage(message), status);
	}

    protected ResponseEntity processErrorResponse(JSONObject message, HttpStatus status) {
        return new ResponseEntity(message, status);
    }

    @ExceptionHandler({ ValidationException.class })
    public ResponseEntity<ResponseMessage> handleException(ValidationException ex) {
        return processErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ ConcurrencyErrorException.class })
    public ResponseEntity<ResponseMessage> handleException(ConcurrencyErrorException ex) {
        return processErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({ AsyncRequiredException.class })
    public ResponseEntity<ResponseMessage> handleException(AsyncRequiredException ex) {
        return processErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({ ServiceDefinitionDoesNotExistException.class, ServiceInstanceNotRetrievableException.class })
    public ResponseEntity<ResponseMessage> handleException(Exception ex) {
        return processErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceInstanceExistsException.class)
    public ResponseEntity<ResponseMessage> handleException(ServiceInstanceExistsException ex) {
        return processErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ServiceInstanceDoesNotExistException.class)
    public ResponseEntity<ResponseMessage> handleException(ServiceInstanceDoesNotExistException ex) {
        return processErrorResponse(HttpStatus.GONE);
    }

    @ExceptionHandler(ServiceInstanceNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleException(ServiceInstanceNotFoundException ex){
        return processErrorResponse(HttpStatus.NOT_FOUND);
    }

}
