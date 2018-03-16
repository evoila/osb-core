package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.model.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

/** @author Johannes Hiemer. */
public abstract class BaseController {

	private final Logger log = LoggerFactory.getLogger(BaseController.class);


	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorMessage> handleException(HttpMessageNotReadableException ex, HttpServletResponse response) {
	    return processErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleException(MethodArgumentNotValidException ex, 
			HttpServletResponse response) {
	    BindingResult result = ex.getBindingResult();
	    String message = "Missing required fields:";
	    for (FieldError error: result.getFieldErrors()) {
	    	message += " " + error.getField();
	    }
		return processErrorResponse(message, HttpStatus.UNPROCESSABLE_ENTITY);
	}


	@ExceptionHandler(Exception.class)
	@GetMapping(value = { "/error" })
	public ResponseEntity<ErrorMessage> handleException(Exception ex, 
			HttpServletResponse response) {
		log.warn("Exception", ex);
		String errMessage = "";
		if (ex != null && ex.getMessage() != null)
			errMessage = ex.getMessage();

	    return processErrorResponse(errMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}


	protected ResponseEntity<ErrorMessage> processErrorResponse(String message, HttpStatus status) {
		return new ResponseEntity<>(new ErrorMessage(message), status);
	}

}
