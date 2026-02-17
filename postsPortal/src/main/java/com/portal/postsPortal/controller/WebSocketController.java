package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.Conversation;
import com.portal.postsPortal.model.Notification;
import com.portal.postsPortal.model.NotificationMessage;
import com.portal.postsPortal.model.User;
import com.portal.postsPortal.repository.ConversationRepository;
import com.portal.postsPortal.repository.NotificationRepository;
import com.portal.postsPortal.repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final ConversationRepository conversationRepository;

    private final UserRepository userRepository;


    public WebSocketController(SimpMessagingTemplate messagingTemplate, NotificationRepository notificationRepository, ConversationRepository conversationRepository, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(NotificationMessage message) {
        System.out.println("Sending message: " + message);

        Conversation conversation = conversationRepository.findById(message.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        User user1 = conversation.getUser1();
        User user2 = conversation.getUser2();

        sendNotificationToUser(user1, message);

        sendNotificationToUser(user2, message);

        triggerPageRefresh(user1.getId(), user2.getId());
    }

    private void sendNotificationToUser(User recipient, NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/conversation/" + message.getConversationId(), message);

        Notification notification = new Notification(
                message.getMessage(),
                message.getConversationId(),
                recipient.getId(),
                LocalDateTime.now()
        );
        notificationRepository.save(notification);

        System.out.println("Notification sent to " + recipient.getFirstName() + " " + recipient.getLastName());
    }

    private void triggerPageRefresh(Long senderId, Long receiverId) {
        messagingTemplate.convertAndSend("/topic/refresh/" + receiverId, "refresh");
    }
}