package atm.std.chatbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String username;
    private Date connectedAt;
    private boolean active;
    
    public User() {
    }
    
    public User(String username) {
        this.username = username;
        this.connectedAt = new Date();
        this.active = true;
    }
} 