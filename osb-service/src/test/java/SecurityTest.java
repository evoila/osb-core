import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.Application;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
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
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

/**
 * @author Marco Di Martino
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
@ContextConfiguration
@ActiveProfiles("default")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class SecurityTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;

    private ServiceInstanceRequest request;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void A_performGetNotAuthenticated() throws Exception {

        mockMvc.perform(get("/v2/catalog").header("X-Broker-API-Version:", "2.13"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void B_performPutWithCredentials_isAccepted() throws Exception {

        Map<String, String> context=new HashMap<>();
        context.put("platform", "cloudfoundry");
        context.put("some_field", "some_data");

        ObjectMapper mapper = new ObjectMapper();

        request=new ServiceInstanceRequest("sample-local", "5678-1234", "org-guid-here", "space-guid-here", context);

        mockMvc.perform(put("/v2/service_instances/1234-5678-91011?accepts_incomplete=true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .header("X-Broker-API-Version:", "2.13")
                .with(user("admin").password("cloudfoundry")))
                .andExpect(status().isAccepted());
    }

    @Test
    public void C_performPutWithoutCredentials_isUnauthorized() throws Exception {

        Map<String, String> context=new HashMap<>();
        context.put("platform", "cloudfoundry");
        context.put("some_field", "some_data");

        ObjectMapper mapper = new ObjectMapper();

        request=new ServiceInstanceRequest("sample-local", "5678-1234", "org-guid-here", "space-guid-here", context);

        mockMvc.perform(put("/v2/service_instances/1234-5678-91011?accepts_incomplete=true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .header("X-Broker-API-Version:", "2.13"))
                .andExpect(status().isUnauthorized());
    }



}




