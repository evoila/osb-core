/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rabbitmq.client.AMQP;
import de.evoila.cf.broker.bean.ExistingEndpointBean;
import de.evoila.cf.broker.bean.impl.ExistingEndpointBeanImpl;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.sample.CouchDbCustomImplementation;
import de.evoila.cf.broker.service.sample.raw.CouchDbService;
import de.evoila.cf.cpi.existing.ExistingServiceFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;

/**
 * @author Johannes Hiemer.
 * @author Marco Di Martino
 *
 */
@Service
public class CouchDbBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

	private SecureRandom random = new SecureRandom();

	@Autowired
	private ExistingEndpointBeanImpl endpointBean;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.BindingServiceImpl#createCredentials(
	 * java.lang.String, de.evoila.cf.broker.model.ServiceInstance,
	 * de.evoila.cf.broker.model.ServerAddress)
	 */

    private CouchDbService openConnection(ExistingEndpointBeanImpl endpointBean, String database) throws ServiceBrokerException {

    String ip = endpointBean.getHosts().get(0);
    int port = endpointBean.getPort();

    // to pass to createConnection
    List<String> hosts = new ArrayList<>();
    for (String address  : endpointBean.getHosts()){
        hosts.add(address);
    }
    log.info("Opening connection to "+ ip + ":" + port);

    CouchDbService conn = new CouchDbService();

    try {
        conn.createConnection(hosts, port, database, endpointBean.getUsername(), endpointBean.getPassword());
    }catch(UnknownHostException e){

        log.info("Could not establish connection", e);
        throw new ServiceBrokerException("Could not establish connection", e);
    }
    return conn;
}


	private CouchDbService openConnection(ExistingEndpointBeanImpl endpointBean) throws ServiceBrokerException {

	    String ip = endpointBean.getHosts().get(0);
	    int port = endpointBean.getPort();

	    // to pass to createConnection
        List<String> hosts = new ArrayList<>();
        for (String address  : endpointBean.getHosts()){
            hosts.add(address);
        }
        log.info("Opening connection to "+ ip + ":" + port);

        CouchDbService conn = new CouchDbService();

        try {
			conn.createConnection(hosts, port, endpointBean.getDatabase(), endpointBean.getUsername(), endpointBean.getPassword());
        }catch(UnknownHostException e){

            log.info("Could not establish connection", e);
            throw new ServiceBrokerException("Could not establish connection", e);
        }
        return conn;
    }

	@Override
	protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
			ServerAddress host) throws ServiceBrokerException {

		log.info("Binding the CouchDB Service...");

        CouchDbService service = openConnection(endpointBean);

        SecureRandom pw = new SecureRandom();
		/* setting credentials */
        String username = bindingId;
        String password = new BigInteger(130, pw).toString(32);
        String database = serviceInstance.getId();

        CouchDbService admin_to_db = openConnection(endpointBean, database);

        ArrayList<Object > adminPass = new ArrayList<Object>(){{
            add(admin_to_db.getCouchDbClient());
            add(endpointBean.getPassword());
        }};
        try {
            CouchDbCustomImplementation.bindRoleToDatabaseWithPassword(service, database, username, password, adminPass);
        }catch(Exception e){
            throw new ServiceBrokerException("Error while binding role", e);
        }

        Map<String, Object> credentials = new HashMap<>();

        credentials.put("username", username);
        credentials.put("password", password);
        credentials.put("database", database);

//		credentials.put("uri", dbURL);

        String dbURL = String.format("couchdb://%s:%s@%s:%d/%s", username,
                password, host.getIp(), host.getPort(),
                serviceInstance.getId());

        return credentials;
    }

	@Override
	public void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {

		log.info("Unbinding the CouchDB Service...");

		CouchDbService service = openConnection(endpointBean);

		JsonObject toRemove = service.getCouchDbClient().find(JsonObject.class, "org.couchdb.user:"+bindingId);
		service.getCouchDbClient().remove(toRemove);

		service = openConnection(endpointBean, serviceInstance.getId());
        JsonObject security_doc = service.getCouchDbClient().find(JsonObject.class, "_security");
        SecurityDocument sd = new Gson().fromJson(security_doc, SecurityDocument.class);
        sd.getAdmins().deleteName(bindingId);
        sd.getMembers().deleteName(bindingId);

        JsonObject security = (JsonObject)new Gson().toJsonTree(sd);
        try {
            CouchDbCustomImplementation.send_put(service, serviceInstance.getId(), service.getConfig().getUsername(),
                    endpointBean.getPassword(), security.toString());
        }catch(Exception e){
            throw new ServiceBrokerException("An error has occurred while deleting binding", e);
        }
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.BindingServiceImpl#bindRoute(de.evoila.
	 * cf.broker.model.ServiceInstance, java.lang.String)
	 */
	@Override
	protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
		throw new UnsupportedOperationException();
	}
	
    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }
}