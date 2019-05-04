package projekti.entities;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageComment extends AbstractPersistable<Long> {

    @OneToOne
    private Account user;
    
    @ManyToOne
    private Message message;

    private LocalDateTime dateTime;
    private String content;

}
