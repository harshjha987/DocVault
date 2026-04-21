package com.harsh.project.Service;

import com.harsh.project.Dto.FileUploadResponse;
import com.harsh.project.Entity.File;
import com.harsh.project.Entity.Folder;
import com.harsh.project.Entity.User;
import com.harsh.project.Exception.FileStorageException;
import com.harsh.project.Exception.ResourceNotFoundException;
import com.harsh.project.Exception.UnauthorizedAccessException;
import com.harsh.project.Mapper.FileMapper;
import com.harsh.project.Repository.FileRepository;


import com.harsh.project.Repository.FolderRepository;
import com.harsh.project.Repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;

    private final FileMapper fileMapper;

    private final UserRepository userRepository;

    private final S3Service s3Service;

    public FileService(FileRepository fileRepository, FolderRepository folderRepository, FileMapper fileMapper, UserRepository userRepository, S3Service s3Service){
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.fileMapper = fileMapper;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }



    public ResponseEntity<FileUploadResponse> uploadFile(MultipartFile multipartFile , String folderId) throws IOException {

        // Save actual file to disk
        String originalName = multipartFile.getOriginalFilename();

        String fileName = UUID.randomUUID() + "_" + originalName;
        s3Service.uploadFile(fileName, multipartFile);
//        try {
//            multipartFile.transferTo(destination);
//        } catch (IOException e) {
//            throw new FileStorageException("Could not save file: " + originalName);
//        }

        User currentUser = getCurrentUser();

        // Save metadata to DB
        File file = new File();
        file.setFileName(fileName);
        file.setOriginalName(originalName);
        file.setFileType(multipartFile.getContentType());
        file.setSize(multipartFile.getSize());
        file.setUploadedAt(Instant.now());
        file.setUser(currentUser);
        if(folderId != null){
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(()->new ResourceNotFoundException("folder not found with this id" + folderId));
            file.setFolder(folder);
        }
        fileRepository.save(file);

        FileUploadResponse res = fileMapper.toResponse(file);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    public ResponseEntity<FileUploadResponse>getFileById(String id){
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "File not found with id: " + id));

        checkOwnership(file, getCurrentUser());
        return new ResponseEntity<>(fileMapper.toResponse(file),HttpStatus.OK);
    }

    public ResponseEntity<byte[]>downloadFile(String id) throws IOException{
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with this id "+ id));

        checkOwnership(file, getCurrentUser());
        try (InputStream inputStream = s3Service.downloadFile(file.getFileName())) {
            byte[] content = inputStream.readAllBytes();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getOriginalName() + "\"")
                    .body(content);
        } catch (IOException e) {
            throw new FileStorageException("Could not download file: " + id);
        }
    }

    public ResponseEntity<String> deleteFile(String id) throws IOException {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));

        checkOwnership(file, getCurrentUser());

//        // Delete from disk
//        Path filePath = Path.of("uploads/" + file.getFileName());
//        Files.deleteIfExists(filePath);

        // Delete from DB
        s3Service.deleteFile(file.getFileName());

        fileRepository.delete(file);

        return new ResponseEntity<>("File deleted", HttpStatus.OK);
    }


    public List<FileUploadResponse> getAllFiles() {

        User currentUser = getCurrentUser();
        return fileRepository.findByUser(currentUser)
                .stream()
                .map(fileMapper::toResponse)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();  // returns the User object we stored in JwtFilter
    }

    public List<FileUploadResponse>searchFiles(String name){
        User user = getCurrentUser();
        return fileRepository.searchByUserAndOriginalNameContainingIgnoreCase(user,name)
                .stream()
                .map(fileMapper::toResponse)
                .collect(Collectors.toList());
    }
    public List<FileUploadResponse> getFilesByFolder(String folderId) {
        User currentUser = getCurrentUser();
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Folder not found with id: " + folderId));
        if (!folder.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You do not have permission to access this folder");
        }
        return folder.getFiles()
                .stream()
                .map(fileMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void checkOwnership(File file, User currentUser) {
        if (!file.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException(
                    "You do not have permission to access this file");
        }
    }
}
