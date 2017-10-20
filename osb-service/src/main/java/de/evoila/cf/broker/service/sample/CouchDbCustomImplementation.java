/**
 * 
 */
package de.evoila.cf.broker.service.sample;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.evoila.cf.broker.bean.impl.ExistingEndpointBeanImpl;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.service.sample.raw.CouchDbService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import de.evoila.cf.broker.service.sample.raw.CouchDbService;
import de.evoila.cf.cpi.existing.CustomExistingService;
import de.evoila.cf.cpi.existing.CustomExistingServiceConnection;

/**
 * @author Johannes Hiemer
 */
@Service
public class CouchDbCustomImplementation implements CustomExistingService {

	private CouchDbService service;

	@Autowired
	private ExistingEndpointBeanImpl endpointBean;

	@Override
	public CustomExistingServiceConnection connection(List<String> hosts, int port, String database, String username,
			String password) throws Exception {

		service = new CouchDbService();

		try{
            service.createConnection(hosts, port, database, username, password);
        }catch(CouchDbException e ){
            throw new ServiceBrokerException("Could not establish connection", e);
        }

		return service;
	}

	public static void bindRoleToDatabaseWithPassword(CouchDbService connection, String database,
			String username, String password) throws Exception {
		    bind(connection, database, username, password, /**/new ArrayList<Object>()/*this obj is to modify (see call from CouchDbBindingService)*/);
	}
	private static void bind(CouchDbService connection, String database,
					 String username, String password, ArrayList<Object> adminPassword) throws Exception {

		/* creation of the user in the _user database*/
		JsonObject js = new JsonObject();
		JsonArray arr = new JsonArray();
		arr.add(database+"_admin");
		js.addProperty("_id", "org.couchdb.user:"+username);
		js.addProperty("name", username);
		js.addProperty("password", password);
		js.add("roles", arr);
		js.addProperty("type", "user");
		connection.getCouchDbClient().save(js);

		/* limit access to the database only for the created user */
		JsonObject main = ((CouchDbClient)adminPassword.get(0)).find(JsonObject.class, "_security");

		JsonObject inside1 = new JsonObject();
		JsonObject inside2 = new JsonObject();
		JsonArray arr1 = new JsonArray();
		JsonArray arr2 = new JsonArray();

		arr1.add(username);
		arr2.add(database+"_admin");
		inside1.add("names", arr1);
		inside1.add("roles", arr2);

		inside2.add("names", arr1);
		inside2.add("roles", new JsonArray());

		main.add("admins", inside1);
		main.add("members", inside2);

		HttpClient c = new DefaultHttpClient();
		HttpPut request = new HttpPut(createUri(connection,
				database,
				connection.getConfig().getUsername(),
				adminPassword.get(1).toString())
				+"/_security"
		);

		StringEntity params =new StringEntity(main.toString(),"UTF-8");

		params.setContentType("application/json");
		request.addHeader("content-type", "application/json");
		request.addHeader("Accept", "application/json");
		request.setEntity(params);

		HttpResponse response = c.execute(request);
		if (response.getStatusLine().getStatusCode() != 200){
			throw new Exception("Error while updating _security document: Database is still open");
		}
	}

	private static String createUri(CouchDbService connection, String database, String username, String password) {

		String baseUri = connection.getCouchDbClient().getBaseUri().toString();
		String credentials = username+":"+password;
		String http = baseUri.substring(0,7);
		baseUri = baseUri.substring(7);

		String uri = http+credentials+"@"+baseUri+database;

		return uri;
	}

	/*@Override
	public void bindRoleToInstanceWithPassword(CustomExistingServiceConnection connection, String database,
			String username, String password) throws Exception {
		if(connection instanceof CouchDbService){
			CouchDbClient client = createNewConnection(database);
			this.bindRoleToDatabaseWithPassword((CouchDbService) connection, database, username, password, client);
		}
	}*/
	@Override
	public void bindRoleToInstanceWithPassword(CustomExistingServiceConnection connection, String database,
											   String username, String password) throws Exception {
		if(connection instanceof CouchDbService){
			ArrayList cred = createNewConnection(database);
			this.bind((CouchDbService) connection, database, username, password, cred);
		}
	}
	private ArrayList<Object> createNewConnection (String database) throws Exception {

		CouchDbService client = new CouchDbService();
		client.createConnection(
				endpointBean.getHosts(),
				endpointBean.getPort(),
				database,
				endpointBean.getUsername(),
				endpointBean.getPassword()
		);

		return new ArrayList<Object>(){{
			add(client.getCouchDbClient());
			add(endpointBean.getPassword());
		}};
	}
	/* for testing */
	public CouchDbService getService() {
		return service;
	}
}