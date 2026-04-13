package com.harsh.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderResponse {

    private String id;

    private String name;

    private Instant createdAt;

    private int fileCount;
}
