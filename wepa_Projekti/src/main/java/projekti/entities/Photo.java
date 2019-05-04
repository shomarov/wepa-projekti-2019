package projekti.entities;

import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo extends FileObject {
    
    @OneToMany
    private List<PhotoComment> comments;

    @ManyToMany
    private Set<Account> likes;

    private String description;

}
