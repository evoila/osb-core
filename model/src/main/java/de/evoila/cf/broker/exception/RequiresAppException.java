package de.evoila.cf.broker.exception;

/**
 * @author Marius Berger
 */
public class RequiresAppException extends ServiceBrokerErrorException {

    public RequiresAppException(String message) {
        super(message);
    }

    @Override
    public String getError() {
        return "RequiresApp";
    }

    @Override
    public String getDescription() {
        return "This Service supports generation of credentials through binding an application only.";
    }
}
