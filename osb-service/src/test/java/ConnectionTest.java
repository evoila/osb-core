import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jndi.toolkit.url.Uri;
import de.evoila.Application;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.persistence.repository.PlatformRepositoryImpl;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
import de.evoila.cf.broker.service.DeploymentServiceImpl;
import de.evoila.cf.broker.service.custom.CouchDbExistingServiceFactory;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.service.sample.CouchDbCustomImplementation;
import de.evoila.cf.broker.service.sample.raw.CouchDbService;
import de.evoila.cf.cpi.existing.ExistingServiceFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightcouch.CouchDbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.InputStream;
import java.util.*;

import static javafx.scene.input.KeyCode.H;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration(classes = Application.class, loader = AnnotationConfigContextLoader.class, initializers = ConfigFileApplicationContextInitializer.class)
public class ConnectionTest {

    @Autowired
    private CouchDbCustomImplementation conn;

    @Autowired
    private CouchDbExistingServiceFactory couchService;

    @Autowired
    private ExistingServiceFactory service;

    @Autowired
    private BindingServiceImpl bindingService;

    @Autowired
    private ServiceInstanceRepository repository;

    @Autowired
    private DeploymentServiceImpl deploymentService;

    /* it is not necessary to have a copy of the application.yml
     in the test path (./test/resource/application.yml)
     */

    @Test
    public void testConnection () throws Exception {

        int port = couchService.getPort();
        String host = couchService.getHosts().get(0);
        String database = couchService.getDatabase();
        String username = couchService.getUsername();
        String password = couchService.getPassword();

        List<String> hosts = new ArrayList<String>();
        hosts.add(host);

        conn.connection(hosts, port, database, username, password);
        assertNotNull(conn.getService());
        assertTrue(conn.getService().isConnected());
        assertEquals(conn.getService().getCouchDbClient().getDBUri().toString(), "http://127.0.0.1:5984/"+couchService.getDatabase()+"/");
    }
    /*@Test
    public void deleteBinding () throws Exception {
        bindingService.deleteBinding("instance_binding", null);
    }*/
    /* **** CouchDbExistingServiceFactory Tests **** */

    @Test
    public void testCreateInstanceFromServiceInstance () throws Exception {

        ServiceInstance s = new ServiceInstance("instance_binding", "service_def", "s", "d", "d", new HashMap<>(), "d");
        Plan p = new Plan();
        service.createInstance(s, p, new HashMap<String, String>());

        HttpClient c = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://"+couchService.getUsername()+":"+couchService.getPassword()+"@"+couchService.getHosts().get(0)+":"+couchService.getPort()+"/"+s.getId());

        HttpResponse response = c.execute(get);

        assertEquals(200, response.getStatusLine().getStatusCode()); // instanz wurde erzeugt
    }

    /*@Test
    public void testBindRoleToInstanceWithPassword () throws Exception {

        String host = "127.0.0.1";

        List<String> hosts = new ArrayList<String>();
        hosts.add(host);
        CouchDbService service = new CouchDbService();
        service.createConnection(hosts, 5984, "_users", "admin", "admin");

        conn.bindRoleToInstanceWithPassword(service, "instance_binding", "username", "password");
    }
    */

    @Test
    public void testBindToRole () throws Exception {

        List<ServerAddress> list = new ArrayList<>();

        ServerAddress sa = new ServerAddress();
        sa.setIp("127.0.0.1");
        sa.setPort(5984);
        sa.setName("127.0.0.1");

        list.add(sa);

        ServiceInstance serviceInstance = new ServiceInstance("instance_binding", "service_def", "s", "d", "d", new HashMap<>(), "d");
        serviceInstance.setHosts(list);
        repository.addServiceInstance("instance_binding", serviceInstance);

        ServiceInstanceBindingResponse serviceInstanceBinding = bindingService.createServiceInstanceBinding("binding_id", serviceInstance.getId(),
                "sample-local", "sample_s_local", false, null);

        //bindingService.deleteServiceInstanceBinding("instance_binding");
    }

    @Test
    public void testDeleteInstanceBinding () throws Exception {
        bindingService.deleteServiceInstanceBinding("binding_id");
    }
    /*
    @Test
    public void testDeleteInstance () throws Exception {

        String host = "127.0.0.1";

        List<String> hosts = new ArrayList<String>();
        hosts.add(host);

        CouchDbService service = new CouchDbService();
        service.createConnection(hosts, 5984, "_users", "admin", "admin");
        couchService.deleteDatabase(service,"instance_binding");

    }
    */

    @Test
    public void testSyncDeleteInstance () throws Exception {

        ServiceInstance serviceInstance = new ServiceInstance("234", "service_def", "s", "d", "d", new HashMap<>(), "d");
        deploymentService.syncDeleteInstance(serviceInstance, service);

    }

    @Test
    public void testSimulation () throws Exception {

        /* create instance */
        ServiceInstance serviceInstance = new ServiceInstance("instance_id", "service_def", "s", "d", "d", new HashMap<>(), "d");
        Plan p = new Plan();
        service.createInstance(serviceInstance, p, new HashMap<String, String>());
        HttpClient c = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://"+couchService.getUsername()+":"+couchService.getPassword()+"@"+couchService.getHosts().get(0)+":"+couchService.getPort()+"/"+serviceInstance.getId());
        HttpResponse response = c.execute(get);

        assertEquals(200, response.getStatusLine().getStatusCode());

        /* binding instance */
        List<ServerAddress> list = new ArrayList<>();

        ServerAddress sa = new ServerAddress();
        sa.setIp("127.0.0.1");
        sa.setPort(5984);
        sa.setName("127.0.0.1");

        list.add(sa);
        serviceInstance.setHosts(list);
        repository.addServiceInstance("instance_id", serviceInstance);

        ServiceInstanceBindingResponse serviceInstanceBinding = bindingService.createServiceInstanceBinding("binding_id", serviceInstance.getId(),
                "sample-local", "sample_s_local", false, null);

        /* deleting binding */
        bindingService.deleteServiceInstanceBinding("binding_id");

        /* deleting instance */
        deploymentService.syncDeleteInstance(serviceInstance, service);


    }
}
