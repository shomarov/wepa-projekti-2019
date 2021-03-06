package projekti.services;

import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekti.entities.FriendRequest;
import projekti.repositories.FriendRequestRepository;

@Service
public class FriendRequestService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Transactional
    public void sendRequest(String userFrom, String userTo) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserFrom(accountService.loadByUsername(userFrom));
        friendRequest.setUserTo(accountService.loadByProfileLink(userTo));
        friendRequest.setDateTime(LocalDateTime.now());
        friendRequestRepository.save(friendRequest);
    }

    public List<FriendRequest> loadRequestsTo(String username) {
        return friendRequestRepository.findByUserTo(accountService.loadByUsername(username));
    }

    public List<FriendRequest> loadRequestsFrom(String username) {
        return friendRequestRepository.findByUserFrom(accountService.loadByUsername(username));
    }

    @Transactional
    public void confirmRequest(String userFrom, String userTo) {
        accountService.loadByProfileLink(userFrom).getFriends().add(accountService.loadByUsername(userTo));
        accountService.loadByUsername(userTo).getFriends().add(accountService.loadByProfileLink(userFrom));
        deleteRequest(userFrom, userTo);
    }

    @Transactional
    public void deleteRequest(String userFrom, String userTo) {
        Long requestId = friendRequestRepository.findByUserFromAndUserTo(accountService.loadByUsername(userFrom), accountService.loadByProfileLink(userTo)).getId();
        friendRequestRepository.deleteById(requestId);
    }

    @Transactional
    public void deleteFriend(String user1, String user2) {
        accountService.loadByUsername(user1).getFriends().remove(accountService.loadByProfileLink(user2));
        accountService.loadByProfileLink(user2).getFriends().remove(accountService.loadByUsername(user1));
    }

}
