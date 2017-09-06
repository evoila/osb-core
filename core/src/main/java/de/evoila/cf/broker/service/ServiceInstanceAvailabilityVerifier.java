package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * Created by reneschollmeyer, evoila on 05.09.17.
 */
public interface ServiceInstanceAvailabilityVerifier {
    public boolean verifyServiceAvailability(ServiceInstance serviceInstance, boolean useInitialTimeout) throws PlatformException;
}
