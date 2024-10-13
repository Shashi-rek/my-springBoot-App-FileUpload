package com.example.my_spring_boot_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;

import com.example.my_spring_boot_app.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // Additional query methods (if needed)
}



