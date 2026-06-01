package com.harsh.project.Security;

import com.harsh.project.Entity.User;
import com.harsh.project.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // check if user exists, if not create them
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(""); // no password for OAuth users
            newUser.setCreatedAt(Instant.now());
            return userRepository.save(newUser);
        });

        // generate JWT same as normal login
        String token = jwtUtil.generateToken(user.getEmail());

        // redirect to frontend with token in URL
        String redirectUrl = "https://docvault.site/oauth2/callback?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

