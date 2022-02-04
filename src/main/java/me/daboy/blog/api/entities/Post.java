package me.daboy.blog.api.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long author;
    @Column
    private String title;
    @Column
    private String body;
    @Column
    private Timestamp date;
    @Column
    private Long category;
    @Column
    private String image;
    @Column(name="comments_type")
    private Integer commentsType;

    public Post() {
    }

    public Post(Long id, Long author, String title, String body, Timestamp date, Long category, String image, Integer commentsType) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.body = body;
        this.date = date;
        this.category = category;
        this.image = image;
        this.commentsType = commentsType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthor() {
        return author;
    }

    public void setAuthor(Long author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCommentsType() {
        return commentsType;
    }

    public void setCommentsType(Integer commentsType) {
        this.commentsType = commentsType;
    }
}
