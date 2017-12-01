import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.evoila.Application;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.DeploymentServiceImpl;
import de.evoila.cf.broker.service.custom.CouchDbExistingServiceFactory;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.service.sample.CouchDbCustomImplementation;
import de.evoila.cf.broker.service.sample.raw.CouchDbService;
import de.evoila.cf.cpi.existing.ExistingServiceFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration(classes = Application.class, loader = AnnotationConfigContextLoader.class, initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles(profiles={"default", "cluster"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClusterTest {

    @Autowired
    private CouchDbCustomImplementation conn;

    @Autowired
    private CouchDbExistingServiceFactory couchService;

    @Autowired
    private ExistingServiceFactory service;

    @Autowired
    private DeploymentServiceImpl deploymentService;

    @Autowired
    private BindingServiceImpl bindingService;

    @Autowired
    private ServiceInstanceRepository repository;

    private ServiceInstance serviceInstance = new ServiceInstance("instance_binding", "service_def", "s", "d", "d", new HashMap<>(), "d");

    private Logger log = LoggerFactory.getLogger(getClass());
    /*
    * db name: instance_binding
    * replication is enabled:
    * test if db is created on every vm
    *
    * */
    @Test
    public void testA_dbCreation() throws Exception {
        CouchDbService service1 = ((CouchDbService)conn.connection(couchService.getHosts(), couchService.getPort(), couchService.getDatabase(), couchService.getUsername(), couchService.getPassword()));

        String database = "mydb";

        couchService.createDatabase(service1, database);
        assertTrue(service1.getCouchDbClient().context().getAllDbs().contains(database)); // db is on first node

        List<String> ip_node = new ArrayList<>();
        ip_node.add(couchService.getHosts().get(1));

        CouchDbClient service2 = ((CouchDbService)conn.connection(ip_node, couchService.getPort(), couchService.getDatabase(), couchService.getUsername(), couchService.getPassword())).getCouchDbClient();
        assertTrue(service2.context().getAllDbs().contains(database)); // db is on second node

        ip_node.remove(0);
        ip_node.add(couchService.getHosts().get(2));
        CouchDbClient service3 = ((CouchDbService)conn.connection(ip_node, couchService.getPort(), couchService.getDatabase(), couchService.getUsername(), couchService.getPassword())).getCouchDbClient();
        assertTrue(service3.context().getAllDbs().contains(database)); // db is on third node

        /* deleting ... */

        service2.context().deleteDB(database, "delete database");
        assertFalse(service1.getCouchDbClient().context().getAllDbs().contains(database));
    }

    @Test
    public void testB_provisionOnCluster() throws Exception {

        Plan plan = new Plan();
        service.createInstance(serviceInstance, plan, new HashMap<String, String>());

        /* testing on all existing services */

        List<String> existingServices = new ArrayList<>();
        for (String host : service.getHosts()) {

            existingServices.add(host);
            log.info("checking for node at: "+existingServices.get(0));

            CouchDbClient client = ((CouchDbService)conn.connection(existingServices,
                                                                    couchService.getPort(),
                                                                    couchService.getDatabase(),
                                                                    couchService.getUsername(),
                                                                    couchService.getPassword()))
                                    .getCouchDbClient();

            assertNotNull(client.find(JsonObject.class, "org.couchdb.user:instance_binding")); // user created
            String uri = "http://"+couchService.getUsername()+":"+couchService.getPassword()+"@"+host+":"+couchService.getPort()+"/"+serviceInstance.getId();

            HttpResponse response = performGet(uri);

            assertEquals(200, response.getStatusLine().getStatusCode()); // db found

            String uri_sec = uri+"/_security";
            HttpResponse resp = performGet(uri_sec);
            HttpEntity e = resp.getEntity();
            String ent = EntityUtils.toString(e);

            assertTrue(ent.contains("instance_binding")); // access protected to db
            client.shutdown();
            existingServices.remove(host);
        }
        String userTest="userTest";
        String passwordTest="passwordTest";

        /* creation of another normal user in '/_users' db */
        CouchDbClient dbc = ((CouchDbService)conn.connection(couchService.getHosts(),
                                                        couchService.getPort(),
                                                        couchService.getDatabase(),
                                                        couchService.getUsername(),
                                                        couchService.getPassword()))
                                            .getCouchDbClient();
        JsonObject js = new JsonObject();
        JsonArray arr = new JsonArray();
        arr.add("no_role");
        js.addProperty("_id", "org.couchdb.user:"+userTest);
        js.addProperty("name", userTest);
        js.addProperty("password", passwordTest);
        js.add("roles", arr);
        js.addProperty("type", "user");
        dbc.save(js);

        /* check userTest cannot access db 'instance_binding' */
        String uri = "http://"+userTest+":"+passwordTest+"@"+couchService.getHosts().get(0)+":"+couchService.getPort()+"/"+serviceInstance.getId();

        HttpResponse resp = performGet(uri);
        assertEquals(403, resp.getStatusLine().getStatusCode()); // "forbidden": Not allowed to access this db


        /* deleting ... */

        JsonObject j = dbc.find(JsonObject.class,"org.couchdb.user:userTest");

        couchService.deleteServiceInstance(serviceInstance);
        assertFalse(dbc.context().getAllDbs().contains("instance_binding"));
        dbc.remove(j);
        assertFalse(dbc.contains("org.couchdb.user:"+userTest));
        assertFalse(dbc.contains("org.couchdb.user:instance_binding"));

    }

    @Test
    public void testC_bindingInstanceOnCluster() throws Exception {

        Plan plan = new Plan();
        service.createInstance(serviceInstance, plan, new HashMap<String, String>());

        List<ServerAddress> list = new ArrayList<>();

        for (String host : couchService.getHosts()) {
            ServerAddress sa = new ServerAddress();
            sa.setIp(host);
            sa.setPort(couchService.getPort());
            sa.setName("couchdb@"+host);
            list.add(sa);
        }

        serviceInstance.setHosts(list);
        repository.addServiceInstance("instance_binding", serviceInstance);

        assertNotNull(repository.getServiceInstance(serviceInstance.getId()));

        String binding_id ="binding_id";

        ServiceInstanceBindingResponse serviceInstanceBinding = bindingService.createServiceInstanceBinding(binding_id, serviceInstance.getId(),
                "sample-local", "sample_s_local", false, null);

        assertEquals(binding_id, serviceInstanceBinding.getCredentials().get("username"));

        String uri = "http://"+couchService.getUsername()+":"+couchService.getPassword()+"@"+couchService.getHosts().get(0)+":"+couchService.getPort()+"/"+serviceInstance.getId()+"/_security";

        HttpResponse r = performGet(uri);
        HttpEntity ee = r.getEntity();
        String entity = EntityUtils.toString(ee);
        log.info(entity);
        assertTrue(entity.contains("binding_id"));

         /* deleting ... */

        bindingService.deleteServiceInstanceBinding(binding_id);
        deploymentService.syncDeleteInstance(serviceInstance, service);
        CouchDbClient dbc = ((CouchDbService)conn.connection(couchService.getHosts(), couchService.getPort(), couchService.getDatabase(), couchService.getUsername(), couchService.getPassword())).getCouchDbClient();
        assertFalse(dbc.context().getAllDbs().contains("instance_binding"));
        assertFalse(dbc.contains("org.couchdb.user:"+binding_id));
        assertFalse(dbc.contains("org.couchdb.user:instance_binding"));

    }

    public HttpResponse performGet (String uri) throws Exception {

        HttpClient c = new DefaultHttpClient();
        HttpGet get = new HttpGet(uri);
        return c.execute(get);
    }
}
