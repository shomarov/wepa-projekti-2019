package projekti.controllers;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import projekti.services.AccountService;
import projekti.services.FriendRequestService;
import projekti.repositories.FriendRequestRepository;
import projekti.repositories.PhotoAlbumRepository;

@Controller
public class FriendsController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    @GetMapping("/users/{username}/friends")
    public String friends(Model model, @PathVariable String username, @RequestParam(required = false) String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        model.addAttribute("currentuser", accountService.loadByUsername(currentUser));

        if (accountService.loadByUsername(currentUser).getProfilePhoto() != null) {
            model.addAttribute("profilePhoto", accountService.loadByUsername(currentUser).getProfilePhoto().getId());
        }

        if (name != null) {
            model.addAttribute("friends", accountService.loadByUsername(currentUser).getFriends().stream().filter(account -> account.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList()));
        } else {
            model.addAttribute("friends", accountService.loadByUsername(currentUser).getFriends());
        }

        model.addAttribute("requestsReceived", friendRequestService.loadRequestsTo(currentUser));
        return "friends";
    }

    @GetMapping("/users/{username}/friends/requestsReceived")
    public String requestsReceived(Model model, @PathVariable String username, @RequestParam(required = false) String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        model.addAttribute("currentuser", accountService.loadByUsername(currentUser));

        if (accountService.loadByUsername(currentUser).getProfilePhoto() != null) {
            model.addAttribute("profilePhoto", accountService.loadByUsername(currentUser).getProfilePhoto().getId());
        }

        model.addAttribute("requestsReceived", friendRequestService.loadRequestsTo(currentUser));
        return "requestsreceived";
    }

    @GetMapping("/users/{username}/friends/requestsSent")
    public String requestsSent(Model model, @PathVariable String username, @RequestParam(required = false) String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        model.addAttribute("currentuser", accountService.loadByUsername(currentUser));

        if (accountService.loadByUsername(currentUser).getProfilePhoto() != null) {
            model.addAttribute("profilePhoto", accountService.loadByUsername(currentUser).getProfilePhoto().getId());
        }

        model.addAttribute("requestsSent", friendRequestService.loadRequestsFrom(currentUser));
        return "requestssent";
    }

    @GetMapping("/users/{username}/search")
    public String getAllUsers(Model model, @PathVariable String username, @RequestParam(required = false) String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        model.addAttribute("currentuser", accountService.loadByUsername(currentUser));
        model.addAttribute("user", accountService.loadByUsername(username));

        if (accountService.loadByUsername(currentUser).getProfilePhoto() != null) {
            model.addAttribute("profilePhoto", accountService.loadByUsername(currentUser).getProfilePhoto().getId());
        }

        if (name != null) {
            model.addAttribute("users", accountService.loadAllAccounts().stream().filter(account -> account.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList()));
        } else {
            model.addAttribute("users", accountService.loadAllAccounts().stream().filter(account -> !(accountService.loadByUsername(currentUser).getFriends().contains(account)))
                    .filter(account -> account != accountService.loadByUsername(currentUser))
                    .collect(Collectors.toList()));
        }
        return "search";
    }

    @PostMapping("/users/{userTo}/add_friend")
    public String sendRequest(@PathVariable String userTo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userFrom = auth.getName();

        friendRequestService.sendRequest(userFrom, userTo);

        return "redirect:/users/" + userFrom + "/search";
    }

    @PostMapping("/users/{userFrom}/delete_request")
    public String deleteRequest(@PathVariable String userFrom) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userTo = auth.getName();

        friendRequestService.deleteRequest(userFrom, userTo);

        return "redirect:/users/" + userTo + "/friends";
    }

    @PostMapping("/users/{userFrom}/confirm_request")
    public String confirmRequest(@PathVariable String userFrom) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userTo = auth.getName();

        friendRequestService.confirmRequest(userFrom, userTo);

        return "redirect:/users/" + userTo + "/friends";
    }

    @PostMapping("/users/{userTo}/cancel_request")
    public String cancelRequest(@PathVariable String userTo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userFrom = auth.getName();

        friendRequestService.deleteRequest(userFrom, userTo);

        return "redirect:/users/" + userFrom + "/friends";
    }

    @PostMapping("/users/{username}/delete_friend")
    public String deleteFriend(@PathVariable String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        friendRequestService.deleteFriend(currentUser, username);

        return "redirect:/users/" + currentUser + "/friends";
    }

}
