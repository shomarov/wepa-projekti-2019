package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekti.services.MessageService;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

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

}
