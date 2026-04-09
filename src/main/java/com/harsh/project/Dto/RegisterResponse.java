package com.harsh.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class RegisterResponse {

    private String id;
    private String email;

    private String name;

    private Instant createdAt;
}
