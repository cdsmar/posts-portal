package com.portal.postsPortal.service;

import com.portal.postsPortal.model.Message;
import com.portal.postsPortal.model.NotificationMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessageNotification(Message message) {
        NotificationMessage notification = new NotificationMessage();
        notification.setFromUser(message.getUser().getFirstName() + " " + message.getUser().getLastName());
        notification.setMessage(message.getContent());
        notification.setConversationId(message.getConversation().getId());

        Long receiverId = message.getConversation().getUser1().getId().equals(message.getUser().getId())
                ? message.getConversation().getUser2().getId()
                : message.getConversation().getUser1().getId();

        messagingTemplate.convertAndSend("/topic/notifications/" + receiverId, notification);
    }
}