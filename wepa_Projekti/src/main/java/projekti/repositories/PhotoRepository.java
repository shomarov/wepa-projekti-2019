package projekti.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.entities.FileObject;
import projekti.entities.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

}
