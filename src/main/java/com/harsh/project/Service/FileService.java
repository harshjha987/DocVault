package com.harsh.project.Service;

import com.harsh.project.Dto.FileUploadResponse;
import com.harsh.project.Entity.File;
import com.harsh.project.Mapper.FileMapper;
import com.harsh.project.Repository.FileRepository;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

@Service
public class FileService {

    private final FileRepository fileRepository;

    private final FileMapper fileMapper;

    public FileService(FileRepository fileRepository, FileMapper fileMapper){
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
    }



    public ResponseEntity<FileUploadResponse> uploadFile(MultipartFile multipartFile) throws IOException {

        // Save actual file to disk
        String originalName = multipartFile.getOriginalFilename();

        String fileName = UUID.randomUUID() + "_" + originalName;
        Path destination = Path.of("uploads/" + fileName);
        Files.createDirectories(destination.getParent());
        multipartFile.transferTo(destination);

        // Save metadata to DB
        File file = new File();
        file.setFileName(fileName);
        file.setOriginalName(originalName);
        file.setFileType(multipartFile.getContentType());
        file.setSize(multipartFile.getSize());
        file.setUploadedAt(Instant.now());
        fileRepository.save(file);

        FileUploadResponse res = fileMapper.toEntity(file);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    public ResponseEntity<FileUploadResponse>getFileById(String id){
        File file = fileRepository.findById(id)
                .orElseThrow(()->
            new RuntimeException("File not found with id"+ id)
        );
        return new ResponseEntity<>(fileMapper.toEntity(file),HttpStatus.OK);
    }

    public ResponseEntity<Resource>downloadFile(String id) throws IOException{
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with this id "+ id));
        Path filePath = Path.of("uploads/"+ file.getFileName());
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename =\""+ file.getOriginalName())
                .body(resource);
    }

}
