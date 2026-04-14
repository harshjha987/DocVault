package com.harsh.project.Repository;

import com.harsh.project.Entity.File;
import com.harsh.project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, String> {

    List<File> findByUser(User user);

    List<File>searchByUserAndOriginalNameContainingIgnoreCase(User user,String name);

}
