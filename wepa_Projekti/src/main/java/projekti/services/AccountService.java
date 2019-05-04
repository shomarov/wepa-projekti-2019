package projekti.services;

import java.util.List;
import javax.transaction.Transactional;
import projekti.repositories.AccountRepository;
import projekti.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import projekti.entities.PhotoAlbum;
import projekti.repositories.PhotoAlbumRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    @Transactional
    public boolean save(String username, String password, String name, String link) {
        if (accountRepository.findByUsername(username) != null) {
            return false;
        }

        Account a = new Account();
        a.setUsername(username);
        a.setPassword(passwordEncoder.encode(password));
        a.setName(name);
        a.setProfileLink(link);
        accountRepository.save(a);

        PhotoAlbum album = new PhotoAlbum();
        album.setUser(a);
        photoAlbumRepository.save(album);

        return true;
    }

    public Account loadByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public List<Account> loadAllAccounts() {
        return accountRepository.findAll();
    }

}
