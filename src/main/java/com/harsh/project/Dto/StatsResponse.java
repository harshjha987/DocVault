package com.harsh.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsResponse {
    private long totalFiles;

    private long totalSize;

    private long totalFolders;
}
