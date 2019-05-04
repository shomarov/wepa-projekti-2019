package projekti.repositories;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.entities.Account;
import projekti.entities.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByReceiver(Account username, Pageable pageable);

}
