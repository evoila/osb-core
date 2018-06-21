package de.evoila.cf.broker.controller;

import de.evoila.cf.broker.bean.EndpointConfiguration;
import de.evoila.cf.broker.model.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by reneschollmeyer, evoila on 21.06.18.
 */
@RestController
@RequestMapping(value = "/v2")
public class ExtensionController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(ExtensionController.class);

    private Map<String, List<Server>> servers;

    @Autowired
    private EndpointConfiguration endpointConfiguration;

    @PostConstruct
    private void init() {
        servers = new HashMap<>();
        if(endpointConfiguration.getCustom() != null) {
            servers.put("servers", endpointConfiguration.getCustom());
        }
    }

    @GetMapping(value = "/extensions")
    public ResponseEntity<Map<String, List<Server>>> getExtensions() {
        return new ResponseEntity<>(servers, HttpStatus.OK);
    }
}
