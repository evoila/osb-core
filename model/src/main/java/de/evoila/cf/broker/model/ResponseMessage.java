package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to send errors back to the cloud controller.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 *
 */
public class ResponseMessage<T> {

	@JsonProperty("message")
	private T message;

	public ResponseMessage(T message) {
		this.message = message;
	}

	public T getMessage() {
		return message;
	}

	public void setMessage(T message) {
		this.message = message;
	}
	
}
