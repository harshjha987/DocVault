package com.harsh.project.Service;

import com.harsh.project.Dto.LoginRequest;
import com.harsh.project.Dto.LoginResponse;
import com.harsh.project.Dto.RegisterRequest;
import com.harsh.project.Dto.RegisterResponse;
import com.harsh.project.Entity.User;
import com.harsh.project.Repository.UserRepository;
import com.harsh.project.Security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    //REGISTER User
    public ResponseEntity<RegisterResponse> register(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setCreatedAt(Instant.now());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        RegisterResponse response = new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt()
        );

        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    //LOGIN User
    public ResponseEntity<LoginResponse>login(LoginRequest request){
        //1.authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        String token = jwtUtil.generateToken(request.getEmail());
        return new ResponseEntity<>(new LoginResponse(token),HttpStatus.OK);
    }
}
