package com.harsh.project.Security;

import com.harsh.project.Entity.User;
import com.harsh.project.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Spring Security calls this method automatically during login
    // it passes the email (username) and expects UserDetails back
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Step 1 — find user in your DB by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));

        // Step 2 — convert your User entity into Spring Security's UserDetails
        // Spring Security doesn't understand your User entity directly
        // it understands UserDetails — so we convert
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())       // the username (email)
                .password(user.getPassword())         // the hashed password
                .authorities("USER")                  // role (we'll keep it simple)
                .build();
    }
}
