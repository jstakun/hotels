package net.gmsworld.server.mongo;

import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.mongodb.DB;
import com.mongodb.Mongo;
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
		
		String mongoHost = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
		String mongoPort = System.getenv("OPENSHIFT_MONGODB_DB_PORT");
		String mongoUser = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
		String mongoPassword = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
		String mongoDBName = "cache";
		int port = Integer.decode(mongoPort);
		
		Mongo mongo = null;
		try {
			mongo = new Mongo(mongoHost, port);
			logger.log(Level.INFO, "Connected to database");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Couldn't connect to MongoDB: " + e.getMessage(), e);
		}

		mongoDB = mongo.getDB(mongoDBName);

		if (mongoDB.authenticate(mongoUser, mongoPassword.toCharArray()) == false) {
			System.out.println("Failed to authenticate DB ");
		}

		this.initDatabase(mongoDB);

	}

	public DB getDB() {
		return mongoDB;
	}
	
	private void initDatabase(DB mongoDB) {
		logger.log(Level.INFO, "Initializing mongo database...");
	}
}
