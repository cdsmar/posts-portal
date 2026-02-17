package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.Post;
import com.portal.postsPortal.model.User;
import com.portal.postsPortal.repository.PostRepository;
import com.portal.postsPortal.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    // Common method to fetch user from the session
    private User getUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (User) session.getAttribute("user"); // This assumes the user is set in the session after OAuth login
    }

    @GetMapping("/dashboard")
    public String showAllPosts(@RequestParam(required = false) String query, Model model, HttpServletRequest request) {
        User user = getUserFromSession(request);  // Retrieve user from session

        if (user != null) {
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
    public String showUserPosts(Model model, HttpServletRequest request) {
        User user = getUserFromSession(request);  // Retrieve user from session

        if (user != null) {
            List<Post> posts = postRepo.findByUser(user);

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "my-posts";
        }

        return "redirect:/login";
    }

    @GetMapping("/find-hobby")
    public String showHobbyPosts(Model model, HttpServletRequest request) {
        User user = getUserFromSession(request);  // Retrieve user from session

        if (user != null) {
            List<Post> posts = postRepo.findByCategory("Hobby");

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/find-studying")
    public String showStudyingPosts(Model model, HttpServletRequest request) {
        User user = getUserFromSession(request);  // Retrieve user from session

        if (user != null) {
            List<Post> posts = postRepo.findByCategory("Studying");

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/find-group")
    public String showGroupPosts(Model model, HttpServletRequest request) {
        User user = getUserFromSession(request);  // Retrieve user from session

        if (user != null) {
            List<Post> posts = postRepo.findByCategory("Group");

            model.addAttribute("posts", posts);
            model.addAttribute("user", user);

            return "dashboard";
        }

        return "redirect:/login";
    }
}
