package com.portal.postsPortal.model;

public class NotificationMessage {

    private String fromUser;
    private String message;
    private Long conversationId;
    private String timestamp;

    public NotificationMessage() {}

    public NotificationMessage(String fromUser, String message, Long conversationId, String timestamp) {
        this.fromUser = fromUser;
        this.message = message;
        this.conversationId = conversationId;
        this.timestamp = timestamp;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "fromUser='" + fromUser + '\'' +
                ", message='" + message + '\'' +
                ", conversationId=" + conversationId +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}