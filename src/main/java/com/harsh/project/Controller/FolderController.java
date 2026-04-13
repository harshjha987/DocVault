package com.harsh.project.Controller;


import com.harsh.project.Dto.FolderRequest;
import com.harsh.project.Dto.FolderResponse;
import com.harsh.project.Service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {
    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/create")
    public ResponseEntity<FolderResponse>createFolder(@RequestBody FolderRequest request){
        return folderService.createFolder(request);
    }
    @GetMapping("/allFolders")
    public List<FolderResponse> getAllFolders(){
        return folderService.getAllFolders();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String>deleteFolder(@PathVariable String id){
        return folderService.deleteFolder(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FolderResponse>renameFolder(@PathVariable String id,@RequestBody FolderRequest request){
        return  folderService.renameFolder(id, request);
    }
}
