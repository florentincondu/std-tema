package atm.std.chatbackend.repository;

   import atm.std.chatbackend.model.Message;
   import org.springframework.data.jpa.repository.JpaRepository;

   public interface MessageRepository extends JpaRepository<Message, Long> {
   }