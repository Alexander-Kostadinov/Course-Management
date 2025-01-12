package com.gotinite.course_management.repositories;

import com.gotinite.course_management.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByName(String name);

    Optional<Course> findByName(String name);

    List<Course> findByStatus(String status);

    @Query("SELECT c FROM Course c WHERE c.teacher.id = :id")
    List<Course> findByTeacherId(@Param("id") Long id);

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :id")
    List<Course> findByStudentId(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Course c SET c.name = :name WHERE c.id = :id")
    void updateNameById(@Param("name") String name, @Param("id") Long id);

    @Modifying
    @Query("UPDATE Course c SET c.status = :status WHERE c.id = :id")
    void updateStatusById(@Param("status") String status, @Param("id") Long id);
}
