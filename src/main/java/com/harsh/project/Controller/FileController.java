package com.harsh.project.Controller;

import com.harsh.project.Dto.FileUploadResponse;
import com.harsh.project.Dto.StatsResponse;
import com.harsh.project.Entity.File;
import com.harsh.project.Service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file
    ,@RequestParam(value = "folderId", required = false) String folderId) throws IOException {
            return fileService.uploadFile(file,folderId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileUploadResponse>getFileById(@PathVariable String id){
        return fileService.getFileById(id);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]>downloadFile(@PathVariable String id) throws IOException{

        return fileService.downloadFile(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable String id) throws IOException {
        return fileService.deleteFile(id);
    }

    @GetMapping("/allFiles")
    public List<FileUploadResponse> getAllFiles(){
        return fileService.getAllFiles();
    }


    @GetMapping("/search")
    public List<FileUploadResponse>searchFiles(@RequestParam("name")String name){
        return fileService.searchFiles(name);
    }

    @GetMapping("/folders/{folderId}/files")
    public List<FileUploadResponse> getFilesByFolder(@PathVariable String folderId) {
        return fileService.getFilesByFolder(folderId);
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse>getStats(){
        return ResponseEntity.ok(fileService.getStats());
    }


}
