/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import com.google.gson.JsonObject;
import de.evoila.cf.broker.bean.impl.ExistingEndpointBeanImpl;
import de.evoila.cf.broker.service.sample.raw.CouchDbService;
import org.lightcouch.Replicator;
import org.lightcouch.ReplicatorDocument;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.sample.CouchDbCustomImplementation;
import de.evoila.cf.cpi.existing.CustomExistingService;
import de.evoila.cf.cpi.existing.CustomExistingServiceConnection;
import de.evoila.cf.cpi.existing.ExistingServiceFactory;
//import de.evoila.cf.broker.service.sample.raw.CouchDbService;
import org.lightcouch.CouchDbClient;
//import org.lightcouch.CouchDbInfo;
import org.lightcouch.CouchDbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes Hiemer
 * @author Marco Di Martino
 *
 */


@Service
public class CouchDbExistingServiceFactory extends ExistingServiceFactory {

    private static final String HTTP = "http://";
	private static final String PREFIX_ID = "org.couchdb.user:";
	private static final String DB = "db-";

	@Autowired
	private CouchDbCustomImplementation couchService;

    @Autowired
    private ExistingEndpointBeanImpl endpointBean;

	@Override
	protected void createInstance(CustomExistingServiceConnection connection, String database) throws PlatformException {
		if(connection instanceof CouchDbService)
			createDatabase((CouchDbService) connection, database);
	}
	public void createDatabase(CouchDbService couchdb, String database) throws PlatformException {
		log.info("Creating the CouchDB Service...");
		database=DB+database;
		try {

			CouchDbClient client = couchdb.getCouchDbClient();
			client.context().createDB(database);
		}catch (CouchDbException e){
        throw new PlatformException("Could not create to the database", e);
        }
	}

	@Override
	protected void deleteInstance(CustomExistingServiceConnection connection, String database) throws PlatformException {
		if(connection instanceof CouchDbService)
			deleteDatabase((CouchDbService) connection, database);
	}

	public void deleteDatabase(CouchDbService couchdb, String database) throws PlatformException {
		log.info("Deleting the CouchDB Service...");
		database=DB+database;
		try{
			couchdb.getCouchDbClient().context().deleteDB(database, "delete database");
			JsonObject user = couchdb.getCouchDbClient().find(JsonObject.class, PREFIX_ID+database);
			couchdb.getCouchDbClient().remove(user);
		}catch(CouchDbException e) {
			throw new PlatformException("could not delete from the database", e);
		}
	}

	@Override
	protected CustomExistingService getCustomExistingService() {
		return couchService;
	}

	@Override
	public ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
		log.info("Executing Post Provisioning the CouchDB Service...");
		
		return serviceInstance;
	}

}