package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.*;
import com.portal.postsPortal.repository.*;
import com.portal.postsPortal.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class ConversationController {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public ConversationController(UserRepository userRepository,
                                  ConversationRepository conversationRepository,
                                  MessageRepository messageRepository,
                                  SimpMessagingTemplate messagingTemplate, NotificationRepository notificationRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    private User getLoggedInUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            String username = authentication.getName();
            return userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        }
    }
    @GetMapping("/conversation/{userId}")
    public String showConversation(@PathVariable Long userId, Model model, Authentication authentication) {
        User loggedInUser = getLoggedInUser(authentication);

        User contactUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Contact user not found"));

        Conversation conversation = conversationRepository
                .findConversationBetweenUsers(loggedInUser, contactUser)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    if (loggedInUser.getId() < contactUser.getId()) {
                        newConversation.setUser1(loggedInUser);
                        newConversation.setUser2(contactUser);
                    } else {
                        newConversation.setUser1(contactUser);
                        newConversation.setUser2(loggedInUser);
                    }
                    return conversationRepository.save(newConversation);
                });

        List<Message> messages = messageRepository.findByConversationOrderByTimestampAsc(conversation);

        model.addAttribute("conversation", conversation);
        model.addAttribute("messages", messages);

        User otherUser = conversation.getUser1().getId().equals(loggedInUser.getId()) ? conversation.getUser2() : conversation.getUser1();
        model.addAttribute("otherUser", otherUser);

        model.addAttribute("currentUserId", loggedInUser.getId());

        return "conversation";
    }


    @PostMapping("/conversation/{userId}/send-message")
    public String sendMessage(@PathVariable Long userId,
                              @RequestParam String content,
                              Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);
        User contactUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Conversation conversation = conversationRepository
                .findConversationBetweenUsers(loggedInUser, contactUser)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    if (loggedInUser.getId() < contactUser.getId()) {
                        newConversation.setUser1(loggedInUser);
                        newConversation.setUser2(contactUser);
                    } else {
                        newConversation.setUser1(contactUser);
                        newConversation.setUser2(loggedInUser);
                    }
                    return conversationRepository.save(newConversation);
                });

        Message message = new Message();
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setUser(loggedInUser);
        message.setConversation(conversation);
        messageRepository.save(message);

        Notification notification = new Notification(
                loggedInUser.getFirstName() + " " + loggedInUser.getLastName() + ": " + content,
                conversation.getId(),
                contactUser.getId(),
                LocalDateTime.now()
        );
        notificationRepository.save(notification);

        NotificationMessage notificationMessage = new NotificationMessage(
                loggedInUser.getFirstName() + " " + loggedInUser.getLastName(),
                content,
                conversation.getId(),
                LocalDateTime.now().toString()
        );

        messagingTemplate.convertAndSend("/topic/conversation/" + conversation.getId(), notificationMessage);

        if (loggedInUser.getId() != contactUser.getId()) {
            messagingTemplate.convertAndSend("/topic/refresh/" + contactUser.getId(), "refresh");
        }
        return "redirect:/conversation/" + userId;
    }
}