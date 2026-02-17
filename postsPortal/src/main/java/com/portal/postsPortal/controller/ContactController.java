package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.Contact;
import com.portal.postsPortal.model.User;
import com.portal.postsPortal.repository.ContactRepository;
import com.portal.postsPortal.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ContactController {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    public ContactController(UserRepository userRepository, ContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
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

    @GetMapping("/users")
    public String showUsers(@RequestParam(value = "query", required = false) String query,
                            Model model,
                            Authentication authentication) {

        User loggedInUser = getLoggedInUser(authentication);

        List<User> users;

        if (query == null || query.isEmpty()) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    query, query, query
            );
        }

        users.remove(loggedInUser);

        model.addAttribute("users", users);
        model.addAttribute("query", query);

        return "users-list";
    }

    @PostMapping("/add-contact/{userId}")
    public String addContact(@PathVariable Long userId, Authentication authentication, RedirectAttributes redirectAttributes) {
        User loggedInUser = getLoggedInUser(authentication);

        User contactUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (contactRepository.existsByUserAndContactUser(loggedInUser, contactUser)) {
            redirectAttributes.addFlashAttribute("contactAlreadyExists", true);
        } else {
            Contact contact = new Contact(loggedInUser, contactUser, LocalDateTime.now());
            contactRepository.save(contact);
            redirectAttributes.addFlashAttribute("contactAdded", true);
        }

        return "redirect:/users";
    }

    @PostMapping("/remove-contact/{userId}")
    public String removeContact(@PathVariable Long userId, Authentication authentication) {
        User loggedInUser = getLoggedInUser(authentication);

        User contactUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = contactRepository.findByUserAndContactUser(loggedInUser, contactUser)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        contactRepository.delete(contact);

        return "redirect:/contacts";
    }

    @GetMapping("/contacts")
    public String showContacts(Model model, Authentication authentication) {
        User loggedInUser = getLoggedInUser(authentication);

        List<Contact> contacts = contactRepository.findByUser(loggedInUser);

        model.addAttribute("contacts", contacts);
        return "contacts-list";
    }
}