package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.Contact;
import com.portal.postsPortal.model.Conversation;
import com.portal.postsPortal.model.Message;
import com.portal.postsPortal.model.User;
import com.portal.postsPortal.repository.ContactRepository;
import com.portal.postsPortal.repository.ConversationRepository;
import com.portal.postsPortal.repository.MessageRepository;
import com.portal.postsPortal.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

import com.portal.postsPortal.model.NotificationMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class ConversationController {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ConversationController(UserRepository userRepository,
                                  ConversationRepository conversationRepository,
                                  MessageRepository messageRepository,
                                  SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/conversation/{userId}")
    public String showConversation(@PathVariable Long userId, Model model, Authentication authentication) {
        String username = authentication.getName();
        User loggedInUser = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
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

        return "conversation";
    }

    @PostMapping("/conversation/{userId}/send-message")
    public String sendMessage(@PathVariable Long userId,
                              @RequestParam String content,
                              Authentication authentication) {

        User loggedInUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
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

        // Send notification to recipient
        NotificationMessage notification = new NotificationMessage(
                loggedInUser.getFirstName() + " " + loggedInUser.getLastName(),
                content,
                conversation.getId()
        );

        messagingTemplate.convertAndSend("/topic/notifications/" + contactUser.getId(), notification);

        return "redirect:/conversation/" + userId;
    }
}