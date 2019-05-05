package projekti.services;

import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import projekti.entities.MessageComment;
import projekti.entities.Message;
import projekti.repositories.MessageCommentRepository;
import projekti.repositories.MessageRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageCommentRepository commentRepository;

    @Transactional
    public void postMessage(String userFrom, String userTo, String content) {
        Message message = new Message();
        message.setSender(accountService.loadByUsername(userFrom));
        message.setReceiver(accountService.loadByUsername(userTo));
        message.setDateTime(LocalDateTime.now());
        message.setContent(content);
        messageRepository.save(message);
        accountService.loadByUsername(userTo).getMessages().add(message);
    }

    public List<Message> loadUserMessages(String username) {
        Pageable pageable = PageRequest.of(0, 25, Sort.by("dateTime").descending());

        return messageRepository.findByReceiver(accountService.loadByUsername(username), pageable);
    }

    public Message loadMessage(Long id) {
        return messageRepository.getOne(id);
    }

    @Transactional
    public void addCommentToMessage(Long messageId, String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        MessageComment comment = new MessageComment();
        comment.setSender(accountService.loadByUsername(currentUser));
        comment.setDateTime(LocalDateTime.now());
        comment.setContent(content);

        commentRepository.save(comment);

        if (messageRepository.getOne(messageId).getComments().size() == 10) {
            messageRepository.getOne(messageId).getComments().remove(0);
        }
        messageRepository.getOne(messageId).getComments().add(comment);
    }

    public List<MessageComment> loadUserMessageComments(Long messageId) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dateTime").descending());

        return commentRepository.findByMessageId(messageId, pageable);
    }

}
