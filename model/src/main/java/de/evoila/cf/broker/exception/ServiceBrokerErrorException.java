package de.evoila.cf.broker.exception;

/**
 * This abstract class is meant for being the base of exceptions that target to handle "Service Broker Errors"
 * of the definition in the Open Service Broker specification.
 * As of date of writing these changes, there are four explicitly mentioned errors:
 * <ul>
 *     <li>AsyncRequired</li>
 *     <li>ConcurrencyError</li>
 *     <li>RequiresApp</li>
 *     <li>MaintenanceInfoConflict</li>
 * </ul>
 *
 * @author Marius Berger
 */
public abstract class ServiceBrokerErrorException extends Exception {

    public ServiceBrokerErrorException() {}

    public ServiceBrokerErrorException(String message) {
        super(message);
    }

    /**
     * Returns the error code to put into the response body.
     * This "code" is a String and not a number!
     * @return the error code to put into the response body
     */
    public abstract String getError();

    /**
     * Returns the description text to put into the response body.
     * @return the description text to put into the response body
     */
    public abstract String getDescription();
}
