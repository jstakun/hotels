package net.gmsworld.server.camel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        //read first value as array
        String key = (String)root.keys().next();
        
        logger.log(Level.INFO, "Splitting json array {0}", key);
        
        JSONArray jsonArray = root.getJSONArray(key);
        
        for(int i=0; i<jsonArray.length(); i++) {
            String jsonMsg = jsonArray.get(i).toString();
            messageList.add(jsonMsg);
        }

        return messageList;

    }

}
