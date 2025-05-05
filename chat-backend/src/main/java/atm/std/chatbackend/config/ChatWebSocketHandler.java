package atm.std.chatbackend.config;

import atm.std.chatbackend.model.Message;
import atm.std.chatbackend.model.User;
import atm.std.chatbackend.repository.MessageRepository;
import atm.std.chatbackend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>(); // mapare session.id -> username
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String payload = textMessage.getPayload();
        Message message = objectMapper.readValue(payload, Message.class);
        
        // Procesăm mesajul în funcție de tip
        switch (message.getType()) {
            case JOIN:
                handleJoinMessage(session, message);
                break;
            case LEAVE:
                handleLeaveMessage(session, message);
                break;
            case CHAT:
            default:
                handleChatMessage(session, message);
                break;
        }
    }

    private void handleJoinMessage(WebSocketSession session, Message message) throws IOException {
        String username = message.getSender();
        
        // Salvăm asocierea session -> username
        sessionUserMap.put(session.getId(), username);
        
        // Verificăm dacă utilizatorul există, dacă nu, îl creăm
        User user = userRepository.findByUsername(username).orElse(new User(username));
        if (!user.isActive()) {
            user.setActive(true);
            user.setConnectedAt(new Date());
        }
        userRepository.save(user);
        
        // Creăm un mesaj de conectare și îl salvăm
        message.setTimestamp(new Date());
        message.setContent(username + " s-a conectat");
        message.setType(Message.MessageType.JOIN);
        messageRepository.save(message);
        
        // Trimitem mesajul de conectare tuturor
        broadcastMessage(message);
        
        // Trimitem lista utilizatorilor actualizată tuturor
        broadcastUserList();
    }
    
    private void handleLeaveMessage(WebSocketSession session, Message message) throws IOException {
        String username = message.getSender();
        
        // Setăm utilizatorul ca inactiv
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
        
        // Creăm un mesaj de deconectare și îl salvăm
        message.setTimestamp(new Date());
        message.setContent(username + " s-a deconectat");
        message.setType(Message.MessageType.LEAVE);
        messageRepository.save(message);
        
        // Trimitem mesajul de deconectare tuturor
        broadcastMessage(message);
        
        // Trimitem lista utilizatorilor actualizată tuturor
        broadcastUserList();
    }
    
    private void handleChatMessage(WebSocketSession session, Message message) throws IOException {
        message.setTimestamp(new Date());
        messageRepository.save(message);
        
        // Trimitem mesajul tuturor
        broadcastMessage(message);
    }
    
    private void broadcastMessage(Message message) throws IOException {
        String messageJson = objectMapper.writeValueAsString(message);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(messageJson));
            }
        }
    }
    
    private void broadcastUserList() throws IOException {
        // Creăm un mesaj special pentru lista de utilizatori
        Message userListMessage = new Message();
        userListMessage.setType(Message.MessageType.SYSTEM);
        userListMessage.setSender("System");
        userListMessage.setTimestamp(new Date());
        userListMessage.setContent(objectMapper.writeValueAsString(userRepository.findByActiveTrue()));
        
        String userListJson = objectMapper.writeValueAsString(userListMessage);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(userListJson));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Obținem username-ul asociat sesiunii
        String username = sessionUserMap.get(session.getId());
        if (username != null) {
            // Creăm și trimitem un mesaj de deconectare
            Message leaveMessage = new Message();
            leaveMessage.setType(Message.MessageType.LEAVE);
            leaveMessage.setSender(username);
            leaveMessage.setContent(username + " s-a deconectat");
            leaveMessage.setTimestamp(new Date());
            
            // Setăm utilizatorul ca inactiv
            userRepository.findByUsername(username).ifPresent(user -> {
                user.setActive(false);
                userRepository.save(user);
            });
            
            // Salvăm mesajul
            messageRepository.save(leaveMessage);
            
            // Trimitem mesajul tuturor
            broadcastMessage(leaveMessage);
            
            // Ștergem asocierea din map
            sessionUserMap.remove(session.getId());
        }
        
        // Eliminăm sesiunea
        sessions.remove(session);
        
        // Trimitem lista utilizatorilor actualizată
        broadcastUserList();
    }
}