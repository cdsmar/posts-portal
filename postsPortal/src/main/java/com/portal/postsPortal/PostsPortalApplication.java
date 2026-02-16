package com.portal.postsPortal;

import com.portal.postsPortal.model.Course;
import com.portal.postsPortal.model.Hobby;
import com.portal.postsPortal.model.Team;
import com.portal.postsPortal.repository.CourseRepository;
import com.portal.postsPortal.repository.HobbyRepository;
import com.portal.postsPortal.repository.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PostsPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostsPortalApplication.class, args);


	}
	@Bean
	CommandLineRunner runner(HobbyRepository hobbyRepo, CourseRepository courseRepo, TeamRepository teamRepo) {
		return args -> {
			if(hobbyRepo.count() == 0){
				hobbyRepo.save(new Hobby(null, "Gaming"));
				hobbyRepo.save(new Hobby(null, "Reading"));
			}
			if(courseRepo.count() == 0){
				courseRepo.save(new Course(null, "Math"));
				courseRepo.save(new Course(null, "CS"));
			}
			if(teamRepo.count() == 0){
				teamRepo.save(new Team(null, "Team Alpha"));
				teamRepo.save(new Team(null, "Team Beta"));
			}
		};
	}

}
