package com.gotinite.course_management.repositories;

import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Enrollment;
import com.gotinite.course_management.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentAndCourse(Student student, Course course);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :id AND e.status = :status")
    List<Enrollment> findByStatus(@Param("id") Long id, @Param("status") String status);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :id")
    List<Enrollment> findEnrollmentsByCourseId(@Param("id") Long id);

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :id")
    List<Enrollment> findEnrollmentsByStudentId(@Param("id") Long id);
}
