package com.portal.postsPortal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private Long conversationId;
    private Long recipientUserId;
    private LocalDateTime timestamp;

    private boolean isRead;

    public Notification() {}

    public Notification(String content, Long conversationId, Long recipientUserId, LocalDateTime timestamp, boolean isRead) {
        this.content = content;
        this.conversationId = conversationId;
        this.recipientUserId = recipientUserId;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public Notification(String content, Long conversationId, Long recipientUserId, LocalDateTime timestamp) {
        this.content = content;
        this.conversationId = conversationId;
        this.recipientUserId = recipientUserId;
        this.timestamp = timestamp;
    }

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

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(Long recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }
}