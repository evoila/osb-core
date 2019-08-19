package de.evoila.cf.broker.exception;

import org.springframework.http.HttpStatus;

/**
 * 
 * @author Dennis Mueller, Marius Berger
 *
 */
public class AsyncRequiredException extends ServiceBrokerErrorException {

	@Override
	public String getError() {
		return "AsyncRequired";
	}

	@Override
	public String getDescription() {
		return "This Service Plan requires client support for asynchronous service operations.";
	}
}
