package projekti;

import projekti.repositories.AccountRepository;
import projekti.entities.Account;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import projekti.entities.PhotoAlbum;
import projekti.repositories.PhotoAlbumRepository;

@Component
public class TestAccounts {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PhotoAlbumRepository photoAlbumRepository;

    @Transactional
    public void populateList() {
        Account a = new Account();
        a.setName("Tony Stark");
        a.setUsername("ironman");
        a.setPassword(passwordEncoder.encode("password"));
        a.setProfileLink("Ironman");
        accountRepository.save(a);

        PhotoAlbum album = new PhotoAlbum();
        album.setUser(a);
        photoAlbumRepository.save(album);

        Account b = new Account();
        b.setName("Steve Rogers");
        b.setUsername("captain");
        b.setPassword(passwordEncoder.encode("password"));
        b.setProfileLink("Captain America");
        accountRepository.save(b);

        album = new PhotoAlbum();
        album.setUser(b);
        photoAlbumRepository.save(album);

        Account c = new Account();
        c.setName("Bruce Banner");
        c.setUsername("hulk");
        c.setPassword(passwordEncoder.encode("password"));
        c.setProfileLink("Hulk");
        accountRepository.save(c);

        album = new PhotoAlbum();
        album.setUser(c);
        photoAlbumRepository.save(album);

        Account d = new Account();
        d.setName("Thor Odinson");
        d.setUsername("thor");
        d.setPassword(passwordEncoder.encode("password"));
        d.setProfileLink("Thor");
        accountRepository.save(d);

        album = new PhotoAlbum();
        album.setUser(d);
        photoAlbumRepository.save(album);

        Account e = new Account();
        e.setName("Natasha Romanoff");
        e.setUsername("blackwidow");
        e.setPassword(passwordEncoder.encode("password"));
        e.setProfileLink("Black Widow");
        accountRepository.save(e);

        album = new PhotoAlbum();
        album.setUser(e);
        photoAlbumRepository.save(album);

        Account f = new Account();
        f.setName("Peter Parker");
        f.setUsername("spiderman");
        f.setPassword(passwordEncoder.encode("password"));
        f.setProfileLink("spiderman");
        accountRepository.save(f);

        album = new PhotoAlbum();
        album.setUser(f);
        photoAlbumRepository.save(album);

    }

}
