package com.portal.postsPortal.controller;

import com.portal.postsPortal.model.User;
import com.portal.postsPortal.model.Hobby;
import com.portal.postsPortal.model.Course;
import com.portal.postsPortal.model.Team;
import com.portal.postsPortal.repository.UserRepository;
import com.portal.postsPortal.repository.HobbyRepository;
import com.portal.postsPortal.repository.CourseRepository;
import com.portal.postsPortal.repository.TeamRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ProfileController {

    private final UserRepository userRepo;
    private final HobbyRepository hobbyRepo;
    private final CourseRepository courseRepo;
    private final TeamRepository teamRepo;

    public ProfileController(UserRepository userRepo,
                             HobbyRepository hobbyRepo,
                             CourseRepository courseRepo,
                             TeamRepository teamRepo) {
        this.userRepo = userRepo;
        this.hobbyRepo = hobbyRepo;
        this.courseRepo = courseRepo;
        this.teamRepo = teamRepo;
    }

    @GetMapping("/profile-setup")
    public String showProfileSetup(@RequestParam Long userId, Model model) {
        User user = userRepo.findById(userId).orElseThrow();
        model.addAttribute("user", user);

        List<Hobby> allHobbies = hobbyRepo.findAll();
        List<Course> allCourses = courseRepo.findAll();
        List<Team> allTeams = teamRepo.findAll();

        model.addAttribute("allHobbies", allHobbies);
        model.addAttribute("allCourses", allCourses);
        model.addAttribute("allTeams", allTeams);

        return "profile-setup";
    }

    @PostMapping("/profile-setup")
    public String saveProfileSetup(@RequestParam Long userId,
                                   @RequestParam(required = false) List<Long> hobbies,
                                   @RequestParam(required = false) List<Long> courses,
                                   @RequestParam(required = false) List<Long> teams) {

        User user = userRepo.findById(userId).orElseThrow();

        Set<Hobby> hobbySet = new HashSet<>();
        if (hobbies != null) {
            for (Long id : hobbies) {
                hobbyRepo.findById(id).ifPresent(hobbySet::add);
            }
        }
        user.setHobbies(hobbySet);

        Set<Course> courseSet = new HashSet<>();
        if (courses != null) {
            for (Long id : courses) {
                courseRepo.findById(id).ifPresent(courseSet::add);
            }
        }
        user.setCourses(courseSet);

        Set<Team> teamSet = new HashSet<>();
        if (teams != null) {
            for (Long id : teams) {
                teamRepo.findById(id).ifPresent(teamSet::add);
            }
        }
        user.setTeams(teamSet);

        userRepo.save(user);

        return "redirect:/dashboard";
    }
}