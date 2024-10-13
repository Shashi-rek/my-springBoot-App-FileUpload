package com.example.my_spring_boot_app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.my_spring_boot_app.model.FileEntity;


public interface FileRepository extends JpaRepository<FileEntity, Long> {

}
