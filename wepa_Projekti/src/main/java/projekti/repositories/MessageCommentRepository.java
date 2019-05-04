package projekti.repositories;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.entities.Account;
import projekti.entities.MessageComment;
import projekti.entities.Message;

public interface MessageCommentRepository extends JpaRepository<MessageComment, Long> {
    
    List<MessageComment> findByMessageId(Long messageId, Pageable pageable);

}
