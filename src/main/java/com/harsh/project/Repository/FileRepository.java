package com.harsh.project.Repository;

import com.harsh.project.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<File, String> {

}
