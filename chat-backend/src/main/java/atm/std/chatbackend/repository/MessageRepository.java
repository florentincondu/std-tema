package atm.std.chatbackend.repository;

import atm.std.chatbackend.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {
}