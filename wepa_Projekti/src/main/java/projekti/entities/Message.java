package projekti.entities;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message extends AbstractPersistable<Long> {

    @ManyToOne
    private Account sender;
    
    @ManyToOne
    private Account receiver;
    
    private LocalDateTime dateTime;
    private String content;

    @OneToMany
    private List<MessageComment> comments;

}
