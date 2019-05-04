package projekti.entities;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
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
public class Account extends AbstractPersistable<Long> {

//    @NotEmpty
//    @Size(min = 3, max = 30)
    private String username;

//    @NotEmpty
//    @Size(min = 8, max = 30)
    private String password;

//    @NotEmpty
//    @Size(min = 5, max = 30)
    private String name;

//    @NotEmpty
//    @Size(min = 3, max = 30)
    private String profileLink;

    @OneToOne
    private Photo profilePhoto;

    @ManyToMany
    private List<Account> friends;

    @OneToMany(mappedBy = "userTo")
    private List<FriendRequest> friendRequestSent;

    @OneToMany(mappedBy = "userFrom")
    private List<FriendRequest> friendRequestReceived;

    @OneToMany
    private List<Message> messages;

}
