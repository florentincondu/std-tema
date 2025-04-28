package atm.std.chatbackend.model;

   import com.fasterxml.jackson.annotation.JsonFormat;
   import jakarta.persistence.Entity;
   import jakarta.persistence.GeneratedValue;
   import jakarta.persistence.GenerationType;
   import jakarta.persistence.Id;
   import lombok.Getter;
   import lombok.Setter;

   import java.util.Date;

   @Entity
   @Getter
   @Setter
   public class Message {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       private String username;
       private String message;
       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
       private Date timestamp;

       public Message() {
       }

       public Message(String username, String message, Date timestamp) {
           this.username = username;
           this.message = message;
           this.timestamp = timestamp;
       }
   }