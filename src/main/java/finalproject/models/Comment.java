package finalproject.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Comment {
    @Id
    private long id;
    private String comment;
    private Date created_at;
}
