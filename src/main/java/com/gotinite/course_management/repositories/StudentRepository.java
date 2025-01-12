package com.gotinite.course_management.repositories;

import com.gotinite.course_management.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    Optional<Student> findByEmail(String email);

    List<Student> findByFirstName(String firstName);

    List<Student> findByLastName(String lastName);

    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.id = :id")
    List<Student> findByCourseId(@Param("id") Long id);

    @Query("SELECT s FROM Student s WHERE CONCAT(s.firstName, ' ', s.lastName) = :name")
    List<Student> findByFullName(@Param("name") String name);

    @Modifying
    @Query("UPDATE Student s SET s.email = :email WHERE s.id = :id")
    void updateEmailById(@Param("id") Long id, @Param("email") String email);
}
