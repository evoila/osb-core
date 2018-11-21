/**
 * 
 */
package de.evoila.cf.broker.exception;

/**
 * @author Johannes Hiemer.
 *
 */
public class PlatformException extends Exception {

	private static final long serialVersionUID = 8072466984852751167L;
	
	public PlatformException(String message) {
		super(message);
	}
	
	public PlatformException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PlatformException(Throwable cause) {
		super(cause);
	}

}
