package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projekti.services.AccountService;
import projekti.services.MessageService;
import projekti.repositories.PhotoAlbumRepository;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password,
            @RequestParam String name, @RequestParam String link) {
        if (accountService.save(username, password, name, link)) {
            return "redirect:/home";
        }
        return "redirect:/";
    }

    @GetMapping("/users/{profileLink}")
    public String getProfile(Model model, @PathVariable String profileLink) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        model.addAttribute("currentuser", accountService.loadByUsername(currentUser));
        model.addAttribute("messages", messageService.loadUserMessages(profileLink));
        model.addAttribute("comments", messageService.loadUserMessages(profileLink));

        if (accountService.loadByProfileLink(profileLink).getProfilePhoto() != null) {
            model.addAttribute("profilePhoto", accountService.loadByProfileLink(profileLink).getProfilePhoto().getId());
        }

        if (accountService.loadByProfileLink(profileLink).getUsername().equals(currentUser)) {
            return "myprofile";
        }

        model.addAttribute("user", accountService.loadByProfileLink(profileLink));

        return "profile";
    }

}
