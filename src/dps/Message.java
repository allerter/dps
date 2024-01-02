package dps;
import java.util.Map;

public class Message {
    int id;
    String type;
    Map<String, String> body;

    public Message(int id, String type, Map<String, String> body) {
        this.id = id;
        this.type = type;
        this.body = body;
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
}
