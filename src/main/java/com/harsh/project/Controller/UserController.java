package com.harsh.project.Controller;

import com.harsh.project.Dto.ChangePasswordRequest;
import com.harsh.project.Dto.UpdateProfileRequest;
import com.harsh.project.Dto.UserProfileResponse;
import com.harsh.project.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        return userService.getProfile();
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(request);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        return userService.changePassword(request);
    }

    @DeleteMapping("/account")
    public ResponseEntity<String> deleteAccount() throws IOException {
        return userService.deleteAccount();
    }
}