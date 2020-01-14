package de.evoila.cf.broker.cpi.endpoint.controller;

import de.evoila.cf.broker.cpi.endpoint.EndpointAvailabilityService;
import de.evoila.cf.broker.model.cpi.AvailabilityState;
import de.evoila.cf.broker.model.cpi.EndpointServiceState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EndpointControllerTest {

    private Map<String, EndpointServiceState> services;

    @Mock
    private EndpointAvailabilityService endpointAvailabilityService;

    private EndpointController endpointController;

    @BeforeEach
    void setUp() {
        services = Map.of("abcdef", new EndpointServiceState("firstId", AvailabilityState.AVAILABLE, "gjsfhjd"),
                "ghij", new EndpointServiceState("secondId", AvailabilityState.ERROR, "ertgz"),
                "klmnopq", new EndpointServiceState("thirdId", AvailabilityState.PENDING, "vcirc"),
                "rstuvwxyz", new EndpointServiceState("forthId", AvailabilityState.UNKNOWN, "eibnxvc"));

        endpointController = new EndpointController(endpointAvailabilityService);
    }

    @Test
    void getCatalog() {
        when(endpointAvailabilityService.getServices())
                .thenReturn(services);

        Map<String, EndpointServiceState> catalog = endpointController.getCatalog();
        assertEquals(services, catalog);
    }
}
