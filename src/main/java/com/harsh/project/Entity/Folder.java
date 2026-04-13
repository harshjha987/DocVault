package com.harsh.project.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "folders")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant createdAt;

    //one folder has many files.
    @OneToMany(mappedBy = "folder",cascade = CascadeType.ALL) //file entity owns this relationship.
    private List<File> files;

    @ManyToOne(fetch = FetchType.LAZY) //many folders belong to one user.
    @JoinColumn(name = "user_id",nullable = false)
    private User user;


}
