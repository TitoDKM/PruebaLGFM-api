package me.daboy.blog.api.dtos;

public class CommentDTO {
    private Long id;
    private String author_photo;
    private String author_name;
    private String body;

    public CommentDTO(Long id, String author_photo, String author_name, String body) {
        this.id = id;
        this.author_photo = author_photo;
        this.author_name = author_name;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor_photo() {
        return author_photo;
    }

    public void setAuthor_photo(String author_photo) {
        this.author_photo = author_photo;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
