package com.gotinite.course_management.repositories;

import com.gotinite.course_management.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    boolean existsByEmail(String email);

    Optional<Teacher> findByEmail(String email);

    List<Teacher> findByFirstName(String firstName);

    List<Teacher> findByLastName(String lastName);

    @Query("SELECT t FROM Teacher t WHERE CONCAT(t.firstName, ' ', t.lastName) = :name")
    List<Teacher> findByFullName(@Param("name") String name);

    @Query("SELECT t FROM Teacher t JOIN t.courses c WHERE c.id = :id")
    Optional<Teacher> findByCourseId(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Teacher t SET t.email = :email WHERE t.id = :id")
    void updateEmailById(@Param("email") String email, @Param("id") Long id);
}
