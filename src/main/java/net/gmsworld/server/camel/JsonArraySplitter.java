package net.gmsworld.server.camel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Message;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonArraySplitter {

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Handler
    public List<String> processMessage(Exchange exchange) {

        List<String> messageList = new ArrayList<String>();

        Message message = exchange.getIn();
        String msg = message.getBody(String.class);
        
        JSONObject root = new JSONObject(msg);         
        //find first array
        
        JSONArray jsonArray = null;
        String key = null;
        
        for (Iterator<String> iter = root.keys(); iter.hasNext();) { 
        	key = iter.next();
        	Object o = root.get(key);
        	if (o instanceof JSONArray) {
        		jsonArray = (JSONArray) o;
        		break;
        	}
        }
        
        if (jsonArray != null) {
        	logger.log(Level.INFO, "Splitting json array {0}", key);
        
        	for(int i=0; i<jsonArray.length(); i++) {
        		String jsonMsg = jsonArray.get(i).toString();
        		messageList.add(jsonMsg);
        	}
        } else {
        	logger.log(Level.WARNING, "No json array available in the document");
        }

        return messageList;

    }

}
