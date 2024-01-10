package dps;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


class MessageDeserializer extends StdDeserializer<Message> { 

    public MessageDeserializer() { 
        this(null); 
    } 

    public MessageDeserializer(Class<?> vc) { 
        super(vc); 
    }

    @Override
    public Message deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        int id = (Integer) (node.get("id")).numberValue();
        String type = node.get("type").asText();
        String utc = node.get("utc").asText();
        JsonNode body = node.get("body");
        Entry<String, JsonNode> next;
        ArrayList<String> bodyArrayList = new ArrayList<>();
        Iterator<Entry<String, JsonNode>> fieldsIter = body.fields();
        while (fieldsIter.hasNext()) {
            next = fieldsIter.next();
            bodyArrayList.add(next.getKey());
            bodyArrayList.add(next.getValue().textValue());
        }
        return new Message(id, utc, type, bodyArrayList.toArray(new String[bodyArrayList.size()]));
    }
}

public class Message {
    int id;
    String type;
    String utc;
    Map<String, String> body = new HashMap<String, String>();

    final static ObjectMapper mapper = JsonMapper.builder()
                            .addModule(new JavaTimeModule().addDeserializer(Message.class, new MessageDeserializer()))
                            .build();

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

    public Message() {
        
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
    public LocalDateTime getUtc() {
        return Utils.dateTimeFromString(this.utc);
    }

    public static Message fromJson(String receivedMessage) throws JsonMappingException, JsonProcessingException {
        return mapper.readValue(receivedMessage, Message.class);
    }

    public String toJson() throws JsonProcessingException {

        return mapper.writeValueAsString(this);
    }

    @Override
    public String toString() {
        return String.format("Message(id=%d, utc=%s, body=%s)", this.id, this.utc, this.body.toString());
    }
}