package projekti.controllers;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekti.repositories.MessageRepository;
import projekti.services.AccountService;
import projekti.services.MessageService;

@Controller
public class MessageController {
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private MessageRepository messageRepository;

    @PostMapping("/users/{userTo}/post_message")
    public String postMessage(@PathVariable String userTo, @RequestParam String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userFrom = auth.getName();
        messageService.postMessage(userFrom, userTo, content);

        return "redirect:/users/" + userTo;
    }

    @PostMapping("/users/{username}/messages/{messageid}/post_comment")
    public String postComment(@PathVariable String username, @PathVariable Long messageid, @RequestParam String content) {
        messageService.addCommentToMessage(messageid, content);

        return "redirect:/users/" + username;
    }
    
    @Transactional
    @PostMapping("/users/{username}/messages/{id}/like_message")
    public String likeMessage(@PathVariable String username, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentuser = auth.getName();
        
        messageRepository.getOne(id).getLikes().add(accountService.loadByUsername(currentuser));
        
        return "redirect:/users/" + username;
    }

}
