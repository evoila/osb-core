package de.evoila.cf.broker.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Marco Di Martino, Marius Berger
 */

public class ConcurrencyErrorException extends ServiceBrokerErrorException {

    @Override
    public String getError() {
        return "ConcurrencyError";
    }

    @Override
    public String getDescription() {
        return "Another operation for this Service Instance is in progress.";
    }
}