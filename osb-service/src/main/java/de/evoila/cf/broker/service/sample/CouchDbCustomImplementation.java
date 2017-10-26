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

    private static final String APPLICATION_JSON = "application/json";

    private static final String CONTENT_TYPE = "Content-Type";

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

	public static void bind(CouchDbService connection, String database,
			String username, String password) throws Exception {
            //ArrayList adminClient = createAdminClient(database);
        bindRoleToDatabaseWithPassword(connection, database, username, password, new ArrayList<>()/*this obj is to modify (see call from CouchDbBindingService)*/);
	}
	public static void bindRoleToDatabaseWithPassword(CouchDbService connection, String database,
					 String username, String password, ArrayList<Object> adminClient) throws Exception {

        String match_role = database + "_admin";
		/* creation of the user in the _user database
		* only server admin can access this database */
        JsonObject js = new JsonObject();
        JsonArray arr = new JsonArray();
        arr.add(match_role);
        js.addProperty("_id", "org.couchdb.user:" + username);
        js.addProperty("name", username);
        js.addProperty("password", password);
        js.add("roles", arr);
        js.addProperty("type", "user");
        connection.getCouchDbClient().save(js);

        /*List<String> hosts = new ArrayList<String>();
        hosts.add(connection.getConfig().getHost());
        */
		/* limit access to the database only for the created user
		* Need to connect to database "database" as server admin to make changes
		* to the _security document
		* Cannot retrieve admin password from the configuration client (connection.getConfig().getPassword()==null)
		* cannot retrieve endpointBean from static-context.
		* Need to have variables from parameters */

        JsonObject main = ((CouchDbClient) adminClient.get(0)).find(JsonObject.class, "_security");
        if (main.size() == 0) {
            //create document
            JsonObject inside1 = new JsonObject();
            JsonObject inside2 = new JsonObject();
            JsonArray arr1 = new JsonArray();
            JsonArray arr2 = new JsonArray();

            arr1.add(username);
            arr2.add(database + "_admin");
            inside1.add("names", arr1);
            inside1.add("roles", arr2);

            inside2.add("names", arr1);
            inside2.add("roles", new JsonArray());

            main.add("admins", inside1);
            main.add("members", inside2);
        } else {
            //update document
            JsonArray names = main.get("admins").getAsJsonObject().get("names").getAsJsonArray();
            names.add(username);
            JsonArray members_names = main.get("members").getAsJsonObject().get("names").getAsJsonArray();
            members_names.add(username);
        }

        send_put(connection, database, connection.getConfig().getUsername(),
                adminClient.get(1).toString(), main.toString());
    }

	public static void send_put(CouchDbService connection, String database, String username,
                                   String password, String file_security) throws Exception {

		String baseUri = connection.getCouchDbClient().getBaseUri().toString();
		String credentials = username+":"+password;
        String http = baseUri.substring(0,7);
		baseUri = baseUri.substring(7);

		String uri = http+credentials+"@"+baseUri+database+"/_security";

        HttpClient c = new DefaultHttpClient();

        StringEntity params =new StringEntity(file_security,"UTF-8");

        HttpPut request = new HttpPut(uri);

        params.setContentType(APPLICATION_JSON);
        request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.addHeader("Accept", APPLICATION_JSON);
        request.setEntity(params);

        HttpResponse response = c.execute(request);
        if (response.getStatusLine().getStatusCode() != 200){
            throw new Exception("Error while updating _security document");
        }
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
			ArrayList adminClient = createAdminClient(database);
			this.bindRoleToDatabaseWithPassword((CouchDbService) connection, database, username, password, adminClient);
		}
	}

	/* give access to the database "database" as server admin */
	private ArrayList<Object> createAdminClient (String database) throws Exception {

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