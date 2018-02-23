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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightcouch.CouchDbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 * @author Marco Di Martino
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration(classes = Application.class, loader = AnnotationConfigContextLoader.class, initializers = ConfigFileApplicationContextInitializer.class)
//@ActiveProfiles(profiles={"default", "singlenode"})
@ActiveProfiles("default")
public class CreateInstanceTest {

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

    /* createInstance:
           call to provisionServiceInstance() Method
           creation of the database, creation of a user and first bind with bindRoleToDatabaseWithPassword
       bindingInstance:
            call to bindService() Method
            create credentials for user
            add user to database
            bindRole to the database
     */

    private ServiceInstance serviceInstance = new ServiceInstance("instance_binding", "service_def", "s", "d", "d", new HashMap<>(), "d");

    @Test
    public void testCreateInstanceFromServiceInstance () throws Exception {

        Plan p = new Plan();
        service.createInstance(serviceInstance, p, new HashMap<String, String>());

        CouchDbClient cl = conn.getService().getCouchDbClient();

        assertNotNull(cl.find(JsonObject.class, "org.couchdb.user:db-instance_binding"));

        String uri = "http://"+couchService.getUsername()+":"+couchService.getPassword()+"@"+couchService.getHosts().get(0)+":"+couchService.getPort()+"/"+"db-"+serviceInstance.getId();

        HttpResponse response = performGet(uri);

        assertEquals(200, response.getStatusLine().getStatusCode());

        String uri_sec = uri+"/_security";
        HttpResponse resp = performGet(uri_sec);
        HttpEntity e = resp.getEntity();
        String ent = EntityUtils.toString(e);

        assertTrue(ent.contains("db-instance_binding"));

        /* binding instance to database */
        List<ServerAddress> list = new ArrayList<>();

        ServerAddress sa = new ServerAddress();
        sa.setIp("127.0.0.1");
        sa.setPort(5984);
        sa.setName("127.0.0.1");

        list.add(sa);

        serviceInstance.setHosts(list);
        repository.addServiceInstance("instance_binding", serviceInstance);

        assertNotNull(repository.getServiceInstance(serviceInstance.getId()));



        ServiceInstanceBindingResponse serviceInstanceBinding = bindingService.createServiceInstanceBinding("binding_id", serviceInstance.getId(),
                "sample-local", "5678-1234", false, null);

        assertEquals("binding_id", serviceInstanceBinding.getCredentials().get("username"));

        HttpResponse r = performGet(uri_sec);
        HttpEntity ee = r.getEntity();
        String entity = EntityUtils.toString(ee);

        assertTrue(entity.contains("binding_id"));
    }

    public HttpResponse performGet (String uri) throws Exception {

        HttpClient c = new DefaultHttpClient();
        HttpGet get = new HttpGet(uri);
        return c.execute(get);

    }

    @After
    public void delete () throws Exception {
        bindingService.deleteServiceInstanceBinding("binding_id"); // it's the username
        deploymentService.syncDeleteInstance(getServiceInstance(), service);
    }


    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }
}
