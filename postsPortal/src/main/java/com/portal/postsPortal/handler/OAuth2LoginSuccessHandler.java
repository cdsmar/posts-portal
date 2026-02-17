package com.portal.postsPortal.handler;

import com.portal.postsPortal.model.User;
import com.portal.postsPortal.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        // Find or create the user
        User existingUser = userRepository.findByEmail(email).orElse(null);
        User userToReturn = null;

        if (existingUser == null) {
            // Create a new user if it doesn't exist
            userToReturn = new User();
            userToReturn.setEmail(email);
            userToReturn.setFirstName(firstName);
            userToReturn.setLastName(lastName);
            userRepository.save(userToReturn);
        } else {
            userToReturn = existingUser;
        }

        // Store the user in the session
        HttpSession session = request.getSession();
        session.setAttribute("user", userToReturn);

        // Redirect to the dashboard
        setDefaultTargetUrl("/dashboard");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
