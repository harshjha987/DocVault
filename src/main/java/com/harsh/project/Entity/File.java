package com.harsh.project.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;


@Entity
@Data
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Instant uploadedAt;

    // many files can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @Column(unique = true)
    private String shareToken;

}
