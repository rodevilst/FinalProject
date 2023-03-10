package finalproject.models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Paid {

    @Id
    private Long id;

    @Column(name = "course")
    private String course;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "age")
    private Integer age;

    @Column(name = "course_format")
    private String courseFormat;

    @Column(name = "course_type")
    private String courseType;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "utm")
    private String utm;

    @Column(name = "msg")
    private String message;

    @Column(name = "status")
    private String status;

    @Column(name = "comment")
    private String comment;

    @OneToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "paid_id")
    private List<Comment> comments;

    @Column(name = "sum")
    private Integer sum;

    @Column(name = "alreadyPaid")
    private Integer alreadyPaid;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
