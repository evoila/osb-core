package de.evoila.cf.broker.exception;

public class CatalogIsNotValidException extends Exception {

    public CatalogIsNotValidException(){
        super();
    }

    public CatalogIsNotValidException(String message) {
        super(message);
    }
}
