package finalproject.models;

import javax.persistence.*;

@Table(name = "groups")
@Entity
public class Group {
    @Id
    private int id;
    private String name;
}
