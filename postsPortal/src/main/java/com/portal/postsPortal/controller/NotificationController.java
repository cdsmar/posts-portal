package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.Notification;
import com.portal.postsPortal.model.User;
import com.portal.postsPortal.service.NotificationService;
import com.portal.postsPortal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                String email = ((UserDetails) authentication.getPrincipal()).getUsername();
                return userRepository.findByEmail(email).orElse(null);
            }
            else if (authentication.getPrincipal() instanceof OAuth2User) {
                OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                String email = oauthUser.getAttribute("email");
                return userRepository.findByEmail(email).orElse(null);
            }
        }
        return null;
    }

    @GetMapping("/notifications")
    public String getNotifications(Model model) {
        User user = getAuthenticatedUser();

        if (user != null) {
            List<Notification> notifications = notificationService.getNotificationsForUser(user);
            notifications.sort((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));
            long unreadCount = notifications.stream().filter(notification -> !notification.isRead()).count();
            model.addAttribute("notifications", notifications);
            model.addAttribute("unreadCount", unreadCount);
            return "notifications";
        }

        return "redirect:/login";
    }

    @GetMapping("/notifications/mark-read/{id}")
    public String markAsRead(@PathVariable("id") Long id) {
        notificationService.markNotificationAsRead(id);
        return "redirect:/notifications";
    }
}