package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.Message;
import com.portal.postsPortal.model.Post;
import com.portal.postsPortal.model.User;
import com.portal.postsPortal.repository.MessageRepository;
import com.portal.postsPortal.repository.PostRepository;
import com.portal.postsPortal.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/add-post")
    public String showAddPostForm() {
        return "add-post";
    }

    @PostMapping("/add-post")
    public String addPost(@RequestParam String title,
                          @RequestParam String description,
                          @RequestParam String category,
                          Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("error", "User not authenticated");
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setCategory(category);
        post.setUser(user);

        postRepository.save(post);

        return "redirect:/dashboard";
    }

    @GetMapping("/post-details/{postId}")
    public String showPostDetails(@PathVariable Long postId, Model model) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<Message> messages = messageRepository.findByPostOrderByTimestampDesc(post);

        model.addAttribute("post", post);
        model.addAttribute("messages", messages);

        return "post-details";
    }

    @PostMapping("/post-details/{postId}/message")
    public String sendMessage(@PathVariable Long postId, @RequestParam String content, Authentication authentication) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Message message = new Message(content, user, post);

        messageRepository.save(message);

        return "redirect:/post-details/" + postId;
    }
}