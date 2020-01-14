package de.evoila.cf.broker.service.availability;

public interface AvailabilityVerifier {

    boolean verify(String ip, int port);

}
