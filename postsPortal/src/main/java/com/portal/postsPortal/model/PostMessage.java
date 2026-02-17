package com.portal.postsPortal.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PostMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime timestamp;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;  // This message is tied to a Post

    // Constructor
    public PostMessage(String content, User user, Post post) {
        this.content = content;
        this.user = user;
        this.post = post;
        this.timestamp = LocalDateTime.now();
    }

    public PostMessage() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}

