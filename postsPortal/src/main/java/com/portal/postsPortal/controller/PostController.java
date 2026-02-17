package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.Message;
import com.portal.postsPortal.model.Post;
import com.portal.postsPortal.model.User;
import com.portal.postsPortal.repository.MessageRepository;
import com.portal.postsPortal.repository.PostRepository;
import com.portal.postsPortal.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
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

        User user = null;
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            String username = authentication.getName();
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

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

        String postUrl = "http://localhost:8080/post-details/" + postId;

        model.addAttribute("post", post);
        model.addAttribute("messages", messages);
        model.addAttribute("postUrl", postUrl);

        return "post-details";
    }

    @PostMapping("/post-details/{postId}/message")
    public String sendMessage(@PathVariable Long postId, @RequestParam String content, Authentication authentication) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = null;
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            String username = authentication.getName();
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Message message = new Message(content, user, post);

        messageRepository.save(message);

        return "redirect:/post-details/" + postId;
    }

    @PostMapping("/post-details/{postId}/send-message")
    public String sendMessageUnderPost(@PathVariable Long postId,
                                       @RequestParam String content,
                                       Authentication authentication) {

        User loggedInUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Message message = new Message();
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setUser(loggedInUser);
        message.setPost(post);

        messageRepository.save(message);

        return "redirect:/post-details/" + postId;
    }
}