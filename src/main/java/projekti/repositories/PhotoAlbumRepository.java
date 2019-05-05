package projekti.repositories;

import projekti.entities.PhotoAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import projekti.entities.Account;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {
    
    PhotoAlbum findPhotoAlbumByUser(Account user);

}
