package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.Contact;
import com.portal.postsPortal.model.User;
import com.portal.postsPortal.repository.ContactRepository;
import com.portal.postsPortal.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping("/users")
    public String showUsers(Model model, Authentication authentication) {
        String username = authentication.getName();
        User loggedInUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> allUsers = userRepository.findAll();
        allUsers.remove(loggedInUser);

        model.addAttribute("users", allUsers);
        return "users-list";
    }

    @PostMapping("/add-contact/{userId}")
    public String addContact(@PathVariable Long userId, Authentication authentication) {
        String username = authentication.getName();
        User loggedInUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User contactUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!contactRepository.existsByUserAndContactUser(loggedInUser, contactUser)) {
            Contact contact = new Contact(loggedInUser, contactUser, LocalDateTime.now());
            contactRepository.save(contact);
        }

        return "redirect:/users";
    }

    @PostMapping("/remove-contact/{userId}")
    public String removeContact(@PathVariable Long userId, Authentication authentication) {
        String username = authentication.getName();
        User loggedInUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User contactUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = contactRepository.findByUserAndContactUser(loggedInUser, contactUser)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        contactRepository.delete(contact);

        return "redirect:/contacts";
    }

    @GetMapping("/contacts")
    public String showContacts(Model model, Authentication authentication) {
        String username = authentication.getName();
        User loggedInUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Contact> contacts = contactRepository.findByUser(loggedInUser);

        model.addAttribute("contacts", contacts);
        return "contacts-list";
    }
}