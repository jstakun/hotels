package net.gmsworld.server.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

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
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import com.sun.istack.logging.Logger;

import net.gmsworld.server.mongo.DBConnection;

@RequestScoped
@Path("/cache")
public class CacheService {
	
	private static final String SECOND_LEVEL_CACHE = "second_level_cache";
    private static final Logger logger = Logger.getLogger(CacheService.class);
	
	@Inject
	private DBConnection dbConnection;
	
	private DBCollection getCollection(String name) {
		DB db = dbConnection.getDB();
		DBCollection itemsListCollection = null;
		if (db.collectionExists(name)) {
			itemsListCollection = db.getCollection(name);
	    } else {
	        DBObject options = BasicDBObjectBuilder.start().add("capped", true).add("size", 1000000l).add("max", 50).get();
	        itemsListCollection = db.createCollection("mycollection", options);
	    }
	
		return itemsListCollection;
	}
	
	@GET
	@Produces("application/json")
	@Path("/geojson/{lat}/{lng}")
	public List<String> getAllItems(@PathParam("lat") String latitude, @PathParam("lng") String longitude) {
		List<String> allItemsList = new ArrayList<String>();

		DBCollection collection = this.getCollection(getCollectionId(latitude, longitude));
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
		String collectionId = getCollectionId(latitude, longitude);
		DBCollection collection = this.getCollection(collectionId);
		logger.log(Level.INFO, "Searching for " + collectionId);
		BasicDBObject query = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		query.put("properties.layer", layer);
		DBCursor cursor = collection.find(query, fields);
		cursor.sort(new BasicDBObject("_id", -1)).limit(1);
		try {
			if (cursor.hasNext()) {
				logger.log(Level.INFO, "Document found");
				response = cursor.next().toString();
			} else {
				logger.log(Level.WARNING, "Document not found");
			}
		} finally {
			cursor.close();
		}
		return response;  
	}
	
	@GET
	@Produces("application/json")
	@Path("/{key}")
	public String getLayer(@PathParam("key") String key) {
		String response = null;
		DBCollection collection = getCollection(SECOND_LEVEL_CACHE);
		logger.log(Level.INFO, "Searching for " + SECOND_LEVEL_CACHE);
		BasicDBObject query = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		query.put("key", key);
		DBCursor cursor = collection.find(query, fields);
		cursor.sort(new BasicDBObject("_id", -1)).limit(1);
		try {
			if (cursor.hasNext()) {
				logger.log(Level.INFO, "Document found");
				response = cursor.next().toString();
			} else {
				logger.log(Level.WARNING, "Document not found");
			}
		} finally {
			cursor.close();
		}
		return response;  
	}
	
	@POST
	@Consumes("application/json")
	@Path("/geojson/{lat}/{lng}")
	public Response insertToCache(@PathParam("lat") String latitude, @PathParam("lng") String longitude, String document) {
		String collectionId = getCollectionId(latitude, longitude);
		DBCollection collection = this.getCollection(collectionId);
		logger.log(Level.INFO, "Saving document to colletion " + collectionId);
		DBObject dbo = (DBObject)JSON.parse(document);
		dbo.put("creationDate", new Date());
		WriteResult wr = collection.insert(dbo);
		if (wr.getError() != null) {
			logger.log(Level.SEVERE, "Failed to save document " + wr.getError());
			return Response.status(500).entity("Failed to save document " + wr.getError()).build();
		} else {
			logger.log(Level.INFO, "Document saved");
			return Response.status(200).entity("Document saved").build();
		}  
	}
	
	@POST
	@Consumes("application/json")
	@Path("/{key}")
	public Response insertToCache(@PathParam("key") String key, String document) {
		DBCollection collection = getCollection(SECOND_LEVEL_CACHE);
		logger.log(Level.INFO, "Saving document to colletion " + SECOND_LEVEL_CACHE);
		DBObject dbo = (DBObject)JSON.parse(document);
		dbo.put("key", key);
		dbo.put("creationDate", new Date());
		WriteResult wr = collection.insert(dbo);
		if (wr.getError() != null) {
			logger.log(Level.SEVERE, "Failed to save document " + wr.getError());
			return Response.status(500).entity("Failed to save document " + wr.getError()).build();
		} else {
			logger.log(Level.INFO, "Document saved");
			return Response.status(200).entity("Document saved").build();
		}  
	}
	
	private static String getCollectionId(String latitude, String longitude) {
		return "geojson_" + latitude.replace('.', '_') + "_" + longitude.replace('.', '_');
	}

}
