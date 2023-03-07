package finalproject.models;

import javax.persistence.*;
import java.util.Date;
@Table(name = "comments")
@Entity
public class Comment {
    @Id
    @Column(name = "id")
    private int id;
    private String comment;
    private Date created_at;


}
