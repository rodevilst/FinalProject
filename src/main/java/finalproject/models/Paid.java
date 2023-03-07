package finalproject.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Paid {
    @Id
    private int id;
    @Column(name = "course")
    private Course course;
    private String name;
    private String username;
    private String email;
    private String phone;
    private int age;
    private Course_Format courseFormat;
    private Course_type courseType;
    private Date created;
    private String utm;
    private String msg;
    private Status status;
    @OneToOne
    private Group group;
    @OneToOne
    private Comment comments;
    private int sum;
    private int alreadyPaid;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
