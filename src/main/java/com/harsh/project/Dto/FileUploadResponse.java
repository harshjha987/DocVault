package com.harsh.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {

    private String id;
    private String originalName;
    private String fileType;
    private Long size;
    private Instant uploadedAt;
}
