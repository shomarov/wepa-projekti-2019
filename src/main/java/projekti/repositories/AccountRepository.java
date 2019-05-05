package projekti.repositories;

import projekti.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Long> {

    public Account findByUsername(String username);
    public Account findByProfileLink(String profileLink);

}
