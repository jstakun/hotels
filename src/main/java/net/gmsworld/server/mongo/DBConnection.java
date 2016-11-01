package net.gmsworld.server.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.sun.istack.logging.Logger;

@Named
@ApplicationScoped
public class DBConnection {
	
	private static final Logger logger = Logger.getLogger(DBConnection.class);
	
	private DB mongoDB;

	public DBConnection() {
		super();
	}

	@PostConstruct
	public void afterCreate() {
		
		//Root User:     OPENSHIFT_MONGODB_DB_USERNAME
		//Root Password: OPENSHIFT_MONGODB_DB_PASSWORD
		//Database Name: cache

		//Connection URL: mongodb://$OPENSHIFT_MONGODB_DB_HOST:$OPENSHIFT_MONGODB_DB_PORT/
		
		final String mongoHost = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
		final String mongoPort = System.getenv("OPENSHIFT_MONGODB_DB_PORT");
		final String mongoUser = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
		final String mongoPassword = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
		final String mongoDBName = "cache";
		//final String mongoUrl = System.getenv("OPENSHIFT_MONGODB_DB_URL");
		final int port = Integer.decode(mongoPort);
		
		List<MongoCredential> credentialsList = new ArrayList<MongoCredential>(1);
		credentialsList.add(MongoCredential.createCredential(mongoUser, mongoDBName, mongoPassword.toCharArray()));
		
		try {
			MongoClient client = new MongoClient(new ServerAddress(mongoHost, port), credentialsList);
			mongoDB = client.getDB(mongoDBName);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Couldn't connect to MongoDB: " + e.getMessage(), e);
		}
		
		initDatabase(mongoDB);
	}

	public DB getDB() {
		return mongoDB;
	}
	
	private void initDatabase(DB mongoDB) {
		logger.log(Level.INFO, "Initializing mongo database...");
	}
}
