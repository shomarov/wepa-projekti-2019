package projekti.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import projekti.entities.FileObject;
import projekti.entities.PhotoComment;

public interface PhotoCommentRepository extends JpaRepository<PhotoComment, Long> {
    
    PhotoComment getPhotoCommentByPhoto(FileObject photo);

}
