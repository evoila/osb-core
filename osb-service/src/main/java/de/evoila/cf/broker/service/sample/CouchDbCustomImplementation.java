/**
 * 
 */
package de.evoila.cf.broker.service.sample;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.evoila.cf.broker.bean.impl.ExistingEndpointBeanImpl;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.NamesAndRoles;
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
import de.evoila.cf.broker.model.UserDocument;
import de.evoila.cf.broker.model.SecurityDocument;
//import de.evoila.cf.broker.service.sample.raw.CouchDbService;
import de.evoila.cf.cpi.existing.CustomExistingService;
import de.evoila.cf.cpi.existing.CustomExistingServiceConnection;

/**
 * @author Johannes Hiemer
 * @author Marco Di Martino
 */

@Service
public class CouchDbCustomImplementation implements CustomExistingService {

    private static final String APPLICATION_JSON = "application/json";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String PREFIX_ID = "org.couchdb.user:";

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
            bindRoleToDatabaseWithPassword(connection, database, username, password, new ArrayList<>());
	}
	public static void bindRoleToDatabaseWithPassword(CouchDbService connection, String database,
					 String username, String password, ArrayList<Object> adminClient) throws Exception {

        String match_role = database + "_admin";
		String id = PREFIX_ID+username;
        /*creation of the user in the _user database
		* only server admin can access this database */

        ArrayList<String> user_roles = new ArrayList<String>(){{ add(match_role);}};

        UserDocument userDoc = new UserDocument(id, username, password, user_roles, "user");
        Gson gson = new Gson();
        JsonObject js = (JsonObject)gson.toJsonTree(userDoc);
        connection.getCouchDbClient().save(js);

		/* ** Security document **
		* limit access to the database only for the created user
		* Need to connect to database "database" as server admin to make changes
		* to the _security document
		* Cannot retrieve admin password from the configuration client (connection.getConfig().getPassword()==null)
		* cannot retrieve endpointBean from static-context.
		* Need to have variables from parameters */

        JsonObject main = ((CouchDbClient) adminClient.get(0)).find(JsonObject.class, "_security");
        SecurityDocument sec_doc = null;

        if (main.size() == 0) {
			// create document
            ArrayList<String> admin_names = new ArrayList<>();
            admin_names.add(username);
            ArrayList<String> admin_roles = new ArrayList<>();
            admin_roles.add(database+"_admin");
			NamesAndRoles adm = new NamesAndRoles(admin_names, admin_roles);
			NamesAndRoles mem = new NamesAndRoles(admin_names, new ArrayList<>());
         	sec_doc = new SecurityDocument(adm, mem);

        } else {
            //update document
            sec_doc = gson.fromJson(main, SecurityDocument.class);
            sec_doc.getAdmins().addName(username);
            sec_doc.getMembers().addName(username);
        }
        JsonObject security = (JsonObject)gson.toJsonTree(sec_doc);

        send_put(connection, database, connection.getConfig().getUsername(),
                adminClient.get(1).toString(), security.toString());
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

    public CouchDbService getService() {
		return service;
	}
}