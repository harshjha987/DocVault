package com.harsh.project.Controller;

import com.harsh.project.Dto.FileUploadResponse;
import com.harsh.project.Entity.File;
import com.harsh.project.Service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
            return fileService.uploadFile(file);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileUploadResponse>getFileById(@PathVariable String id){
        return fileService.getFileById(id);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource>downloadFile(@PathVariable String id) throws IOException{

        return fileService.downloadFile(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable String id) throws IOException {
        return fileService.deleteFile(id);
    }

    @GetMapping("/files")
    public List<FileUploadResponse> getAllFiles(){
        return fileService.getAllFiles();
    }


}
