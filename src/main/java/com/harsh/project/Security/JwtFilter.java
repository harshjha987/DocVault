package com.harsh.project.Security;


import com.harsh.project.Entity.User;
import com.harsh.project.Repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;


    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //read the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        //let spring handle it.
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7).trim(); //extracting token removing "Bearer " prefix.

        //extract email from token.
        String email = jwtUtil.extractEmail(token);

        //check if email exists and user is not already authenticated
        //SecurityContextholder is where Spring Security stores
        //who is currently logged in for this request.
        if(email != null && SecurityContextHolder.getContext().getAuthentication()== null){
            User user = userRepository.findByEmail(email).orElse(null);
            if(user!=null && jwtUtil.validateToken(token,email)){
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, List.of()
                );

                //attach request details to authentication.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //tell Spring this user is authenticated.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        //pass request to next controller or filter.
        filterChain.doFilter(request,response);

    }
}
