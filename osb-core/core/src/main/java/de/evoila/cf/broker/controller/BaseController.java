package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.exception.BadHeaderException;
import de.evoila.cf.broker.model.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/** @author Johannes Hiemer.
 *  @author Marco Di Martino */
public abstract class BaseController {

	private final Logger log = LoggerFactory.getLogger(BaseController.class);

//	private static final String header = "x-broker-api-version";

//	private static final String version = "2.13";

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
		return processErrorResponse(message, HttpStatus.BAD_REQUEST); // was unprocessable_entity
	}

	/*@ExceptionHandler(BadHeaderException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(BadHeaderException ex,
														HttpServletResponse response) {
		log.warn("Exception", ex);
		return processErrorResponse(ex.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}


	public static String getHeader() {
		return header;
	}

	public static String getVersion() {
		return version;
	}
*/
	@ExceptionHandler(Exception.class)
	@RequestMapping(value = { "/error" }, method = RequestMethod.GET)
	public ResponseEntity<ErrorMessage> handleException(Exception ex, 
			HttpServletResponse response) {

		log.warn("Exception", ex);
	    return processErrorResponse(ex.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}
	
	protected ResponseEntity<ErrorMessage> processErrorResponse(String message, HttpStatus status) {
		return new ResponseEntity<>(new ErrorMessage(message), status);
	}


}
