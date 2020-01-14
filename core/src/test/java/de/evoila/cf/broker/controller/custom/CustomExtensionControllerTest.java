package de.evoila.cf.broker.controller.custom;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.model.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomExtensionControllerTest {

    @Mock
    private EndpointConfiguration endpointConfiguration;

    private CustomExtensionController controller;

    @BeforeEach
    void setUp() {
        controller = new CustomExtensionController(endpointConfiguration);
    }

    private void validateResponse(Map<String, List<Server>> expectedServers,
                                  ResponseEntity<Map<String, List<Server>>> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedServers, response.getBody());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void callPostConstructWithServers(List<Server> servers) {
        when(endpointConfiguration.getCustom()).thenReturn(servers);
        Method initMethod = ReflectionUtils.findMethod(controller.getClass(), "init").get();
        ReflectionUtils.invokeMethod(initMethod, controller);
    }

    @Test
    void customEndpointsNull() {
        callPostConstructWithServers(null);
        ResponseEntity<Map<String, List<Server>>> response = controller.getExtensions();
        validateResponse(Map.of(), response);
    }

    @Test
    void customEndpointsEmpty() {
        List<Server> customEndpoints = List.of();
        callPostConstructWithServers(customEndpoints);
        ResponseEntity<Map<String, List<Server>>> response = controller.getExtensions();
        validateResponse(Map.of("servers", customEndpoints), response);
    }

    @Test
    void customEndpointsNotEmpty() {
        List<Server> customEndpoints = List.of(new Server("URL1", "IDENTIFIER1"),
                new Server("URL2", "IDENTIFIER2"), new Server("URL3", "IDENTIFIER3"));
        callPostConstructWithServers(customEndpoints);
        ResponseEntity<Map<String, List<Server>>> response = controller.getExtensions();
        validateResponse(Map.of("servers", customEndpoints), response);
    }

}
