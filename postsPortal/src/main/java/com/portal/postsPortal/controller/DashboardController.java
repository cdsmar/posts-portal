package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.Post;
import com.portal.postsPortal.model.User;
import com.portal.postsPortal.repository.PostRepository;
import com.portal.postsPortal.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    private User getAuthenticatedUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                String email = ((UserDetails) authentication.getPrincipal()).getUsername();
                return userRepo.findByEmail(email).orElse(null);
            } else if (authentication.getPrincipal() instanceof OAuth2User) {
                OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                String email = oauthUser.getAttribute("email");
                return userRepo.findByEmail(email).orElse(null);
            }
        }
        return null;
    }

    private String truncateDescription(String description) {
        if (description != null && description.length() > 150) {
            return description.substring(0, 150) + "...";
        }
        return description;
    }

    @GetMapping("/dashboard")
    public String showAllPosts(@RequestParam(required = false) String query, Model model, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        if (user != null) {
            List<Post> posts;

            if (query != null && !query.isEmpty()) {
                posts = postRepo.searchPostsExcludingUser(query, user);
                model.addAttribute("searchQuery", query);
            } else {
                posts = postRepo.findByUserNot(user);
            }

            posts.forEach(post -> post.setDescription(truncateDescription(post.getDescription())));

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/my-posts")
    public String showUserPosts(Model model, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        if (user != null) {
            List<Post> posts = postRepo.findByUser(user);

            posts.forEach(post -> post.setDescription(truncateDescription(post.getDescription())));

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "my-posts";
        }

        return "redirect:/login";
    }

    @GetMapping("/find-hobby")
    public String showHobbyPosts(Model model, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        if (user != null) {
            List<Post> posts = postRepo.findByCategoryAndUserNot("Hobby", user);

            posts.forEach(post -> post.setDescription(truncateDescription(post.getDescription())));

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/find-studying")
    public String showStudyingPosts(Model model, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        if (user != null) {
            List<Post> posts = postRepo.findByCategoryAndUserNot("Studying", user);

            posts.forEach(post -> post.setDescription(truncateDescription(post.getDescription())));

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/find-group")
    public String showGroupPosts(Model model, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        if (user != null) {
            List<Post> posts = postRepo.findByCategoryAndUserNot("Group", user);

            posts.forEach(post -> post.setDescription(truncateDescription(post.getDescription())));

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }
}