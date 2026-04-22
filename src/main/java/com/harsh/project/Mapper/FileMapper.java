package com.harsh.project.Mapper;

import com.harsh.project.Dto.FileUploadResponse;
import com.harsh.project.Entity.File;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {

    public FileUploadResponse toResponse(File file){
        FileUploadResponse response = new FileUploadResponse();
        response.setId(file.getId());
        response.setOriginalName(file.getOriginalName());
        response.setFileType(file.getFileType());
        response.setSize(file.getSize());
        response.setUploadedAt(file.getUploadedAt());
        response.setFolderId(
                file.getFolder()!= null ? file.getFolder().getId() : null
        );
        response.setShareToken(file.getShareToken());

        return response;
    }
}
