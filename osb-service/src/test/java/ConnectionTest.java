import de.evoila.Application;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.DeploymentServiceImpl;
import de.evoila.cf.broker.service.custom.CouchDbExistingServiceFactory;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.service.sample.CouchDbCustomImplementation;
import de.evoila.cf.broker.service.sample.raw.CouchDbService;
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

import java.util.*;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration(classes = Application.class, loader = AnnotationConfigContextLoader.class, initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles(profiles={"default", "singlenode"})
public class ConnectionTest {

    @Autowired
    private CouchDbCustomImplementation conn;

    @Autowired
    private CouchDbExistingServiceFactory couchService;


    @Test
    public void testConnection () throws Exception {

        int port = couchService.getPort();
        List<String> hosts = couchService.getHosts();
        String database = couchService.getDatabase();
        String username = couchService.getUsername();
        String password = couchService.getPassword();

        conn.connection(hosts, port, database, username, password);
        assertNotNull(conn.getService());
        assertTrue(conn.getService().isConnected());
        assertEquals(conn.getService().getCouchDbClient().getDBUri().toString(), "http://"+hosts.get(0)+":"+port+"/"+couchService.getDatabase()+"/");
    }

}
