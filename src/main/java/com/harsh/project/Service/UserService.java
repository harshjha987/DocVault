package com.harsh.project.Service;


import com.harsh.project.Dto.ChangePasswordRequest;
import com.harsh.project.Dto.UpdateProfileRequest;
import com.harsh.project.Dto.UserProfileResponse;
import com.harsh.project.Entity.File;
import com.harsh.project.Entity.User;
import com.harsh.project.Exception.UnauthorizedAccessException;
import com.harsh.project.Repository.FileRepository;
import com.harsh.project.Repository.FolderRepository;
import com.harsh.project.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final FileRepository fileRepository;

    private final S3Service s3Service;

    private final FolderRepository folderRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, FileRepository fileRepository, S3Service s3Service, FolderRepository folderRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.s3Service = s3Service;
        this.folderRepository = folderRepository;
        this.passwordEncoder = passwordEncoder;
    }
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
    public ResponseEntity<UserProfileResponse> getProfile(){
        User user = getCurrentUser();
        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(),user.getName(),user.getEmail(),user.getCreatedAt()
        ));
    }
    public ResponseEntity<String>changePassword(ChangePasswordRequest req){
        User user = getCurrentUser();
        if(!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())){
            throw new UnauthorizedAccessException("Current password is not correct");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password changed successfully");

    }

    public ResponseEntity<UserProfileResponse> updateProfile(UpdateProfileRequest request){
        User user = getCurrentUser();
        user.setName(request.getName());
        userRepository.save(user);
        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(),user.getName(),user.getEmail(),user.getCreatedAt()
        ));
    }

    public ResponseEntity<String>deleteAccount() throws IOException {
        User user = getCurrentUser();
        List<File> files = fileRepository.findByUser(user);
        for(File file : files){
            s3Service.deleteFile(file.getFileName());
        }
        fileRepository.deleteAll(files);
        folderRepository.deleteAll(folderRepository.findByUser(user));
        userRepository.delete(user);
        return ResponseEntity.ok("Account deleted");

    }
}
