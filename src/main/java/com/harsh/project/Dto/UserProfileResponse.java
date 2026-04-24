package com.harsh.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private Instant createdAt;
}
