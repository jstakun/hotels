package net.gmsworld.server.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import net.gmsworld.server.mongo.DBConnection;

@RequestScoped
@Path("/cache")
public class CacheService {
	
	//TODO create collection by lat,lng
	//TODO find by layer //{"type":"FeatureCollection","properties":{"layer":"Testing"},"features":[]});

	@Inject
	private DBConnection dbConnection;
	
	private DBCollection getCollection(String name) {
		DB db = dbConnection.getDB();
		DBCollection itemsListCollection = db.getCollection(name);
		return itemsListCollection;
	}
	
	@GET
	@Produces("application/json")
	@Path("/geojson/{lat}/{lng}")
	public List<String> getAllItems(@PathParam("lat") String latitude, @PathParam("lng") String longitude) {
		List<String> allItemsList = new ArrayList<String>();

		DBCollection collection = this.getCollection(latitude + "_" + longitude);
		DBCursor cursor = collection.find();
		try {
			while (cursor.hasNext()) {
				allItemsList.add(cursor.next().toString());
			}
		} finally {
			cursor.close();
		}

		return allItemsList;
	}
	
	@GET
	@Produces("application/json")
	@Path("/geojson/{layer}/{lat}/{lng}")
	public String getLayer(@PathParam("layer") String layer, @PathParam("lat") String latitude, @PathParam("lng") String longitude) {
		String response = null;
		DBCollection collection = this.getCollection(latitude + "_" + longitude);
		BasicDBObject query = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		query.put("properties.layer", layer);
		DBCursor cursor = collection.find(query, fields);
		cursor.sort(new BasicDBObject("_id", -1)).limit(1);
		try {
			if (cursor.hasNext()) {
				response = cursor.next().toString();
			}
		} finally {
			cursor.close();
		}
		return response;

	}
	
	@POST()
	@Consumes("application/json")
	@Path("/geojson/{lat}/{lng}")
	public Response insertToCache(@PathParam("lat") String latitude, @PathParam("lng") String longitude, String document) {
		DBCollection testing = this.getCollection(latitude + "_" + longitude);
		DBObject dbo = (DBObject)JSON.parse(document);
		WriteResult wr = testing.insert(dbo);
		return Response.status(200).entity("Document status: " + wr.getError()).build();
	}

}
