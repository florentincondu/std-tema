package atm.std.chatbackend.controller;

   import atm.std.chatbackend.model.Message;
   import atm.std.chatbackend.service.MessageService;
   import org.springframework.web.bind.annotation.CrossOrigin;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;

   import java.util.List;

   @RestController
   @RequestMapping("/api/messages")
   @CrossOrigin(origins = "http://chat-frontend-service:90")
   public class MessageController {
       private final MessageService messageService;

       public MessageController(MessageService messageService) {
           this.messageService = messageService;
       }

       @GetMapping
       public List<Message> getMessages() {
           return messageService.getAllMessages();
       }
   }