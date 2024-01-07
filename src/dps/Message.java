package dps;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
    int id;
    String type;
    String utc;
    public Map<String, String> body = new HashMap<String, String>();

    public Message(int id, String utc, String type, String... args) {
        this.id = id;
        this.utc = utc;
        this.type = type;
        
        if (args.length % 2 != 0){
            throw new IllegalArgumentException("Mismatched number of arguments to constructor.");
        }
        for (int i=0 ; i<args.length ; i+=2) {
            body.put(args[i], args[i + 1]);
        }
        
    }

    public int getId() {
        return id;
    }
    public Map<String, String> getBody() {
        return body;
    }
    public String getType() {
        return type;
    }
    public String getUtc() {
        return utc;
    }

    public static Message fromJson(String receivedMessage) throws JsonMappingException, JsonProcessingException {
        return new ObjectMapper().readValue(receivedMessage, Message.class);
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(this);
    }

    @Override
    public String toString() {
        return String.format("Message(id=%d, utc=%s, body=%s)", this.id, this.utc, this.body.toString());
    }
}
