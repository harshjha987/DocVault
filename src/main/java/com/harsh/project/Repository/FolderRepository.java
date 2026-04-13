package com.harsh.project.Repository;

import com.harsh.project.Entity.Folder;
import com.harsh.project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder,String> {

    //get all folders belonging to a user
    List<Folder> findByUser(User user);

    Optional<Folder>findByNameAndUser(String name, User user);
}
