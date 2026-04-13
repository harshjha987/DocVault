package com.harsh.project.Service;

import com.harsh.project.Dto.FolderRequest;
import com.harsh.project.Dto.FolderResponse;
import com.harsh.project.Entity.Folder;
import com.harsh.project.Entity.User;
import com.harsh.project.Exception.DuplicateResourceException;
import com.harsh.project.Exception.ResourceNotFoundException;
import com.harsh.project.Exception.UnauthorizedAccessException;
import com.harsh.project.Repository.FolderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderService {

    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();  // returns the User object we stored in JwtFilter
    }
    private FolderResponse toResponse(Folder folder) {
        int fileCount = folder.getFiles() == null ? 0 : folder.getFiles().size();
        return new FolderResponse(
                folder.getId(),
                folder.getName(),
                folder.getCreatedAt(),
                fileCount
        );
    }
    public ResponseEntity<FolderResponse>createFolder(FolderRequest request){
        User currentUser = getCurrentUser();
        if(folderRepository.findByNameAndUser(request.getName(),currentUser).isPresent()){
            throw new DuplicateResourceException(
                    "Folder already exists with name: " + request.getName()
            );
        }
        Folder folder = new Folder();
        folder.setName(request.getName());
        folder.setCreatedAt(Instant.now());
        folder.setUser(currentUser);
        folderRepository.save(folder);

        return new ResponseEntity<>(toResponse(folder), HttpStatus.CREATED);
    }

    public List<FolderResponse> getAllFolders(){
        User currentUser = getCurrentUser();
        return folderRepository.findByUser(currentUser)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    public ResponseEntity<String>deleteFolder(String id){
        Folder folder = folderRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "folder not found with this id" + id
                ));
        checkOwnership(folder);
        folderRepository.delete(folder);

        return new ResponseEntity<>("Folder deleted",HttpStatus.OK);
    }
    //Rename folder
    public ResponseEntity<FolderResponse>renameFolder(String id, FolderRequest request){
        Folder folder = folderRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("folder not found with this id" + id));
        checkOwnership(folder);
        folder.setName(request.getName());
        folderRepository.save(folder);

        return new ResponseEntity<>(toResponse(folder),HttpStatus.OK);
    }
    private void checkOwnership(Folder folder) {
        User currentUser = getCurrentUser();
        if (!folder.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException(
                    "You do not have permission to access this folder");
        }
    }
}
