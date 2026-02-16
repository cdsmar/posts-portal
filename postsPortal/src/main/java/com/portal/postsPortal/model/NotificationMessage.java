package com.portal.postsPortal.model;


public class NotificationMessage {

    private String fromUser;
    private String message;
    private Long conversationId;

    public NotificationMessage() {}

    public NotificationMessage(String fromUser, String message, Long conversationId) {
        this.fromUser = fromUser;
        this.message = message;
        this.conversationId = conversationId;
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

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "fromUser='" + fromUser + '\'' +
                ", message='" + message + '\'' +
                ", conversationId=" + conversationId +
                '}';
    }
}