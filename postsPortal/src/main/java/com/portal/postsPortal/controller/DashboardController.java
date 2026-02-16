package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.Post;
import com.portal.postsPortal.model.User;
import com.portal.postsPortal.repository.PostRepository;
import com.portal.postsPortal.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashboardController {

    private final PostRepository postRepo;
    private final UserRepository userRepo;

    public DashboardController(PostRepository postRepo, UserRepository userRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/dashboard")
    public String showAllPosts(@RequestParam(required = false) String query, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            User user = userRepo.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Post> posts;

            if (query != null && !query.isEmpty()) {
                posts = postRepo.searchPostsExcludingUser(query, user);
                model.addAttribute("searchQuery", query);
            } else {
                posts = postRepo.findByUserNot(user);
            }

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/my-posts")
    public String showUserPosts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            User user = userRepo.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Post> posts = postRepo.findByUser(user);

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "my-posts";
        }

        return "redirect:/login";
    }

    @GetMapping("/find-hobby")
    public String showHobbyPosts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            User user = userRepo.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Post> posts = postRepo.findByCategory("Hobby");

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/find-studying")
    public String showStudyingPosts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            User user = userRepo.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Post> posts = postRepo.findByCategory("Studying");

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/find-group")
    public String showGroupPosts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            User user = userRepo.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Post> posts = postRepo.findByCategory("Group");

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }
}