package projekti.entities;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoAlbum extends AbstractPersistable<Long> {
    
    @OneToOne
    private Account user;
    
    @OneToMany
    private List<Photo> photos;

}
