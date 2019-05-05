package projekti.repositories;

import java.util.List;
import projekti.entities.FriendRequest;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.entities.Account;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findByUserTo(Account userTo);
    List<FriendRequest> findByUserFrom(Account userFrom);
    FriendRequest findByUserFromAndUserTo(Account userFrom, Account userTo);

}
