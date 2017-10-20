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

import com.google.gson.JsonObject;
import com.rabbitmq.client.AMQP;
import de.evoila.cf.broker.bean.ExistingEndpointBean;
import de.evoila.cf.broker.bean.impl.ExistingEndpointBeanImpl;
import de.evoila.cf.broker.service.sample.CouchDbCustomImplementation;
import de.evoila.cf.broker.service.sample.raw.CouchDbService;
import de.evoila.cf.cpi.existing.ExistingServiceFactory;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.RouteBinding;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;

/**
 * @author Johannes Hiemer.
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

//	private CouchDbService openConnection(ServiceInstance instance) throws ServiceBrokerException {
//
//		ServerAddress address = instance.getHosts().get(0);
//		int port = address.getPort();
//
//		// to pass to createConnection
//		List<String> hosts = null;
//		for (ServerAddress sa  : instance.getHosts()){
//			hosts.add(sa.getName());
//		}
//		log.info("Opening connection to "+ address.getIp() + ":" + address.getPort());
//
//		CouchDbService conn = new CouchDbService();
//
//		try {
//			conn.createConnection(hosts, port, instance.getId(), instance.getId(), instance.getId());
//		}catch(UnknownHostException e){
//
//			log.info("Could not establish connection", e);
//			throw new ServiceBrokerException("Could not establish connection", e);
//		}
//		return conn;
//
//
//	}

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
		
		String dbURL = String.format("couchdb://%s:%s@%s:%d/%s", this.nextSessionId(),
				this.nextSessionId(), host.getIp(), host.getPort(),
				serviceInstance.getId());

        CouchDbService service = openConnection(endpointBean);

        SecureRandom pw = new SecureRandom();
		/* setting credentials */
		String username = bindingId;
		String password = new BigInteger(130, pw).toString(32);
        String database = serviceInstance.getId();

        try {
			CouchDbCustomImplementation.bindRoleToDatabaseWithPassword(service, database, username, password);
		}catch(Exception e){
        	throw new ServiceBrokerException("Error while binding role", e);
		}

		Map<String, Object> credentials = new HashMap<>();

		credentials.put("username", username);
		credentials.put("password", password);
		credentials.put("database", database);

//		credentials.put("uri", dbURL);
		
		return credentials;
	}

	@Override
	public void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {

		log.info("Unbinding the Couchdb Service...");

		CouchDbService service = openConnection(endpointBean);

		JsonObject toRemove = service.getCouchDbClient().find(JsonObject.class, "org.couchdb.user:"+bindingId);
		service.getCouchDbClient().remove(toRemove);
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
