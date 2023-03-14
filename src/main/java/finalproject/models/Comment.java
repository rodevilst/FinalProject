package finalproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Comment {
    @Id
    private long id;
    private String comment;
    private Date created_at;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "paid_id")
    private Paid paid;

    public Comment(long id, String comment, Date created_at) {
        this.id = id;
        this.comment = comment;
        this.created_at = created_at;
    }

    public Paid getPaid() {
        return paid;
    }

    public void setPaid(Paid paid) {
        this.paid = paid;
    }

    public Comment() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
