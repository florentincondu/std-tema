package atm.std.chatbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "messages")
@Data
public class Message {
    @Id
    private String id;
    private String sender;
    private String content;
    private Date timestamp;
    private MessageType type = MessageType.CHAT; // Implicit este un mesaj normal
    
    public enum MessageType {
        CHAT,       // Mesaj normal
        JOIN,       // Mesaj de conectare utilizator
        LEAVE,      // Mesaj de deconectare utilizator
        SYSTEM      // Mesaj de sistem
    }
}