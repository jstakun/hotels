package net.gmsworld.server.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import net.gmsworld.server.mongo.DBConnection;

@RequestScoped
@Path("/cache")
public class CacheService {

	@Inject
	private DBConnection dbConnection;
	
	private DBCollection getTestingCollection() {
		DB db = dbConnection.getDB();
		DBCollection itemsListCollection = db.getCollection("testing");
		return itemsListCollection;
	}
	
	@GET()
	@Produces("application/json")
	public List<String> getAllItems() {
		List<String> allItemsList = new ArrayList<String>();

		DBCollection testing = this.getTestingCollection();
		DBCursor cursor = testing.find();
		try {
			while (cursor.hasNext()) {
				allItemsList.add(cursor.next().toString());
			}
		} finally {
			cursor.close();
		}

		return allItemsList;
	}

}
