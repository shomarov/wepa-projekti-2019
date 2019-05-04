package projekti.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import projekti.TestAccounts;

@Controller
public class DefaultController {

    @Autowired
    private TestAccounts avengers;

    @GetMapping("*")
    public String redirect() {
        return "redirect:/";
    }

    @GetMapping("/")
    public String home() {
        if (isUserLoggedIn()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            return "redirect:/users/" + username;
        }
        return "landing";
    }

    @GetMapping("/avengers")
    public String insertTheAvengers() {
        avengers.populateList();
        return "redirect:/";
    }

    boolean isUserLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
    }

}
