package atm.std.chatbackend.config;

   import atm.std.chatbackend.model.Message;
   import atm.std.chatbackend.service.MessageService;
   import com.fasterxml.jackson.databind.ObjectMapper;
   import org.springframework.stereotype.Component;
   import org.springframework.web.socket.CloseStatus;
   import org.springframework.web.socket.TextMessage;
   import org.springframework.web.socket.WebSocketSession;
   import org.springframework.web.socket.handler.TextWebSocketHandler;

   import java.io.IOException;
   import java.text.SimpleDateFormat;
   import java.util.Date;
   import java.util.Map;
   import java.util.concurrent.ConcurrentHashMap;

   @Component
   public class ChatWebSocketHandler extends TextWebSocketHandler {
       private final MessageService messageService;
       private final ObjectMapper objectMapper = new ObjectMapper();
       private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
       private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

       public ChatWebSocketHandler(MessageService messageService) {
           this.messageService = messageService;
           this.dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
       }

       @Override
       public void afterConnectionEstablished(WebSocketSession session) throws Exception {
           sessions.put(session.getId(), session);
       }

       @SuppressWarnings("unchecked")
    @Override
       public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
           Map<String, String> payload = objectMapper.readValue(message.getPayload(), Map.class);
           String type = payload.get("type");

           if ("join".equals(type)) {
               String username = payload.get("username");
               session.getAttributes().put("username", username);
           } else {
               String username = (String) session.getAttributes().get("username");
               String msg = payload.get("message");
               String timestampStr = payload.get("timestamp");

               if (username != null && msg != null && timestampStr != null && isAscii(msg)) {
                   Date timestamp = dateFormat.parse(timestampStr);
                   Message chatMessage = new Message(username, msg, timestamp);
                   messageService.saveMessage(chatMessage);
                   broadcastMessage(chatMessage);
               }
           }
       }

       @Override
       public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
           sessions.remove(session.getId());
       }

       private void broadcastMessage(Message message) throws IOException {
           String jsonMessage = objectMapper.writeValueAsString(message);
           for (WebSocketSession session : sessions.values()) {
               if (session.isOpen()) {
                   session.sendMessage(new TextMessage(jsonMessage));
               }
           }
       }

       private boolean isAscii(String str) {
           return str != null && str.matches("^[\\x00-\\x7F]*$");
       }
   }