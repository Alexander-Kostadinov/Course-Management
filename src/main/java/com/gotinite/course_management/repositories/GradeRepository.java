package com.gotinite.course_management.repositories;

import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Grade;
import com.gotinite.course_management.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    boolean existsByStudentAndCourse(Student student, Course course);

    @Query("SELECT g FROM Grade g WHERE g.student.id = :id")
    List<Grade> findByStudentId(@Param("id") Long id);

    @Query("SELECT g FROM Grade g WHERE g.course.id = :id")
    List<Grade> findByCourseId(@Param("id") Long id);

    @Query("SELECT g FROM Grade g WHERE g.teacher.id = :id")
    List<Grade> findByTeacherId(@Param("id") Long id);

    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.course.id = :courseId")
    Optional<Grade> findByStudentIdAndCourseId(@Param("studentId") Long studentId,
                                               @Param("courseId") Long courseId);

    @Modifying
    @Query("UPDATE Grade g SET g.value = :value WHERE g.id = :id")
    void updateValueById(@Param("value") Double value, @Param("id") Long id);
}
