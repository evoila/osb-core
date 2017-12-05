import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.util.JSON;
import de.evoila.Application;
import de.evoila.cf.broker.model.*;
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

import java.lang.reflect.Executable;
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
    }

    @Test
    public void testC_check_access_on_db() throws Exception {
        String userTest="userTest";
        String passwordTest="passwordTest";

        /* creation of another normal user in '/_users' db */
        CouchDbClient dbc = ((CouchDbService)conn.connection(couchService.getHosts(),
                                                        couchService.getPort(),
                                                        couchService.getDatabase(),
                                                        couchService.getUsername(),
                                                        couchService.getPassword()))
                                            .getCouchDbClient();
        ArrayList<String> roles = new ArrayList<>();
        roles.add(serviceInstance.getId()+"_admin");
        UserDocument ud = new UserDocument("org.couchdb.user:"+userTest, userTest, passwordTest, new ArrayList<>(), "user");
        JsonObject js = (JsonObject)new Gson().toJsonTree(ud);
        dbc.save(js);

        /* check userTest cannot access db 'instance_binding' */
        String uri = "http://"+userTest+":"+passwordTest+"@"+couchService.getHosts().get(0)+":"+couchService.getPort()+"/"+serviceInstance.getId();

        HttpResponse resp = performGet(uri);
        assertEquals(403, resp.getStatusLine().getStatusCode()); // "forbidden": Not allowed to access this db

        JsonObject j = dbc.find(JsonObject.class,"org.couchdb.user:userTest");
        dbc.remove(j);
        assertFalse(dbc.contains("org.couchdb.user:"+userTest));
    }

    @Test
    public void testD_check_update_security_document () throws Exception {
        String userTest="userTest";
        String passwordTest="passwordTest";

        CouchDbService to_users = ((CouchDbService)conn.connection(couchService.getHosts(),
                couchService.getPort(),
                couchService.getDatabase(),
                couchService.getUsername(),
                couchService.getPassword())
                            );
        /* giving access to db instance_binding */
        conn.bindRoleToInstanceWithPassword(to_users, serviceInstance.getId(), userTest, passwordTest);

        /* getting data ... */
        CouchDbClient dbc = to_users.getCouchDbClient();

        JsonObject j = dbc.find(JsonObject.class,"org.couchdb.user:userTest");
        assertTrue(dbc.contains("org.couchdb.user:"+userTest));


        CouchDbClient to_instance = ((CouchDbService)conn.connection(couchService.getHosts(),
                couchService.getPort(),
                serviceInstance.getId(),
                couchService.getUsername(),
                couchService.getPassword())
                                    ).getCouchDbClient();

        JsonObject jo = to_instance.find(JsonObject.class, "_security");
        String sec_doc = "{\"admins\":{\"names\":[\"instance_binding\",\"userTest\"],\"roles\":[\"instance_binding_admin\"]},\"members\":{\"names\":[\"instance_binding\",\"userTest\"],\"roles\":[]}}";
        assertEquals(sec_doc, jo.toString());

    }

    @Test
    public void testE_check_user_has_binding() throws Exception {

        String userTest="userTest";
        String passwordTest="passwordTest";

        CouchDbClient usr_db = ((CouchDbService)conn.connection(couchService.getHosts(),
                couchService.getPort(),
                couchService.getDatabase(),
                couchService.getUsername(),
                couchService.getPassword())
                                ).getCouchDbClient();

        String uri = "http://"+userTest+":"+passwordTest+"@"+couchService.getHosts().get(0)+":"+couchService.getPort()+"/"+serviceInstance.getId();

        HttpResponse resp = performGet(uri);
        assertEquals(200, resp.getStatusLine().getStatusCode()); // Now user can access db

    }

    @Test
    public void testF_check_unbinding_service() throws Exception {
        /*deleting bindings in the security document and deleting user */
        CouchDbService dbs = ((CouchDbService)conn.connection(couchService.getHosts(),
                couchService.getPort(),
                serviceInstance.getId(),
                couchService.getUsername(),
                couchService.getPassword())
        );

        bindingService.deleteBinding("userTest", serviceInstance);
        JsonObject security = dbs.getCouchDbClient().find(JsonObject.class, "_security");
        String sec_doc = "{\"admins\":{\"names\":[\"instance_binding\"],\"roles\":[\"instance_binding_admin\"]},\"members\":{\"names\":[\"instance_binding\"],\"roles\":[]}}";
        assertEquals(sec_doc, security.toString());

        CouchDbClient dbc = ((CouchDbService)conn.connection(couchService.getHosts(),
                couchService.getPort(),
                couchService.getDatabase(),
                couchService.getUsername(),
                couchService.getPassword()
        )).getCouchDbClient();

        assertFalse(dbc.contains("org.couchdb.user:userTest"));
    }
    @Test
    public void testG_bindingInstanceOnCluster() throws Exception {
        /*
        Plan plan = new Plan();
        service.createInstance(serviceInstance, plan, new HashMap<String, String>());
        */
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
        assertTrue(entity.contains("binding_id"));
    }

    @Test
    public void testH_deleting_instances () throws Exception {
        String binding_id ="binding_id";
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
