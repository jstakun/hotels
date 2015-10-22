package net.gmsworld.server.mongo;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.mongodb.DB;
import com.mongodb.Mongo;

@Named
@ApplicationScoped
public class DBConnection {

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
			System.out.println("Connected to database");
		} catch (UnknownHostException e) {
			System.out.println("Couldn't connect to MongoDB: " + e.getMessage() + " :: " + e.getClass());
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
		
	}
}
