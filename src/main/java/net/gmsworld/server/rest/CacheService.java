package net.gmsworld.server.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

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
	
	@POST()
	@Consumes("application/json")
	public Response insertToCache(JSONObject document) {
		DBCollection testing = this.getTestingCollection();
		DBObject dbo = (DBObject) document;
		WriteResult wr = testing.insert(dbo);
		
		return Response.status(200).entity("Document status: " + wr.toString()).build();
	}

}
