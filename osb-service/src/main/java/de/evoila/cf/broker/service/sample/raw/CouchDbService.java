/**
 *
 */
package de.evoila.cf.broker.service.sample.raw;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import de.evoila.cf.cpi.existing.CustomExistingServiceConnection;

import java.net.UnknownHostException;
import java.util.List;

/**
 * @author Johannes Hiemer
 * @author Marco Di Martino
 * This class contains a specific implementation for the access to the
 * underlying service. For a database for example it contains the access
 * to the connection, the update and create commands etc.
 *
 * This class should not have any dependencies to Spring or other large
 * Frameworks but instead work with the Drivers directly against the native
 * API.
 */
public class CouchDbService implements CustomExistingServiceConnection {

	//private static final String HTTP = "http://";
	private boolean initialized;
	private String host;
	private CouchDbProperties config;
	private int port;

	private CouchDbClient couchDbClient;

	public void createConnection(List<String> hosts, int port, String database, String  username, String  password) throws UnknownHostException {

       	config = new CouchDbProperties();

        config.setDbName(database);
        config.setCreateDbIfNotExist(true);
        config.setProtocol("http");
        config.setHost(hosts.get(0));
        config.setPort(port);
        config.setUsername(username);
        config.setPassword(password);

        //couchDbClient  = new CouchDbClient(database, true, "http", hosts.get(0), port, username, password);
		couchDbClient = new CouchDbClient(config);

		setHost(hosts.get(0));
		setPort(port);

		setInitialized(true);
	}

	public boolean isConnected() {
		return couchDbClient != null && this.initialized;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public CouchDbClient getCouchDbClient() {
		return couchDbClient;
	}

	public void setConfig(CouchDbProperties config) {
		this.config = config;
	}

	public CouchDbProperties getConfig() {
		return config;
	}


	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
