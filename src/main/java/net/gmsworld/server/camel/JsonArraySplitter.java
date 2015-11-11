package net.gmsworld.server.camel;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class JsonArraySplitter {

	@Handler
    public List<String> processMessage(Exchange exchange) {

        List<String> messageList = new ArrayList<String>();

        Message message = exchange.getIn();
        String msg = message.getBody(String.class);

        JSONArray jsonArray = (JSONArray) JSONValue.parse(msg);

        for(int i=0; i<jsonArray.size(); i++) {
            String jsonMsg = jsonArray.get(i).toString();
            messageList.add(jsonMsg);
        }

        return messageList;

    }

}
