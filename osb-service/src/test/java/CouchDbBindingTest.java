import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.evoila.Application;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.custom.CouchDbExistingServiceFactory;
import de.evoila.cf.broker.service.sample.CouchDbCustomImplementation;
import de.evoila.cf.broker.service.sample.raw.CouchDbService;
import de.evoila.cf.cpi.existing.ExistingServiceFactory;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.lightcouch.CouchDbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Marco Di Martino
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration(classes = Application.class, loader = AnnotationConfigContextLoader.class, initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles(profiles={"default", "cluster"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class CouchDbBindingTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CouchDbCustomImplementation conn;

    @Autowired
    private ExistingServiceFactory service;

    @Autowired
    private CouchDbExistingServiceFactory couchService;

    private ServiceInstance serviceInstance = new ServiceInstance("instance_binding", "service_def", "s", "d", "d", new HashMap<>(), "d");

    @Test
    public void test_local () throws Exception {

        ArrayList<String> a = new ArrayList<>();
        a.add("joe");
        a.add("phil");

        ArrayList<String> b = new ArrayList<>();
        b.add("boss");

        ArrayList<String> c = new ArrayList<>();
        c.add("dave");

        ArrayList<String> d = new ArrayList<>();
        d.add("producer");
        d.add("consumer");

        NamesAndRoles adm = new NamesAndRoles(a, b);
        NamesAndRoles mem = new NamesAndRoles(c, d);

        SecurityDocument sd = new SecurityDocument(adm, mem);
        Gson gson = new Gson();
        String doc = gson.toJson(sd);

        String sec_doc = "{\"admins\":{\"names\":[\"joe\",\"phil\"],\"roles\":[\"boss\"]},\"members\":{\"names\":[\"dave\"],\"roles\":[\"producer\",\"consumer\"]}}";
        assertEquals(sec_doc, doc);


        String user_doc = "{\"_id\":\"org.lala\",\"name\":\"couchdb\",\"password\":\"couchdb\",\"roles\":[],\"type\":\"user\"}";

        UserDocument userDoc = new UserDocument("org.lala", "couchdb", "couchdb", new ArrayList<>(), "user");
        String json = gson.toJson(userDoc);
        assertEquals(user_doc, json);

        SecurityDocument dd = gson.fromJson(doc, SecurityDocument.class);
        log.info(dd.getAdmins().getNames().get(0));


    }

    @Test
    public void test_on_instance() throws Exception {

        Plan p = new Plan();
        service.createInstance(serviceInstance, p, new HashMap<String, String>());

        CouchDbClient cl = ((CouchDbService)conn.connection(couchService.getHosts(),
                                                            couchService.getPort(),
                                                            serviceInstance.getId(),
                                                            couchService.getUsername(),
                                                            couchService.getPassword()
                                                            )
                            ).getCouchDbClient();

        JsonObject j=cl.find(JsonObject.class, "_security");
        assertNotNull(j);

        cl.context().deleteDB(serviceInstance.getId(), "delete database");

        CouchDbClient cl1 = ((CouchDbService)conn.connection(couchService.getHosts(),
                couchService.getPort(),
                couchService.getDatabase(),
                couchService.getUsername(),
                couchService.getPassword()
        )
        ).getCouchDbClient();
        JsonObject j1=cl1.find(JsonObject.class, "org.couchdb.user:db-instance_binding");
        cl1.remove(j1);
    }
}
