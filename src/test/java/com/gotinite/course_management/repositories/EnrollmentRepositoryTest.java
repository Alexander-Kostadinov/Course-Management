package com.gotinite.course_management.repositories;

import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Enrollment;
import com.gotinite.course_management.models.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Sql({
        "/sql/data.sql"
})
class EnrollmentRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void testExistsByStudentAndCourse() {
        Course course = courseRepository.findAll().getFirst();
        Student student = studentRepository.findAll().getFirst();
        assertThat(enrollmentRepository.existsByStudentAndCourse(student, course))
                .isEqualTo(true);
        Student newStudent = studentRepository.findAll().get(1);
        assertThat(enrollmentRepository.existsByStudentAndCourse(newStudent, course))
                .isEqualTo(false);
    }

    @Test
    void testFindByStatus() {
        List<Enrollment> enrollments = enrollmentRepository.findByStatus(1L,"Successful");
        assertThat(enrollments.getFirst().getId())
                .isEqualTo(1L);
    }

    @Test
    void testFindByCourseId() {
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByCourseId(2L);
        assertThat(enrollments.getFirst().getStatus())
                .isNotNull()
                .isEqualTo("Unsuccessful");
    }

    @Test
    void testFindByStudentId() {
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentId(1L);
        assertThat(enrollments.getFirst().getStatus())
                .isEqualTo("Successful");
        assertThat(enrollments.getLast().getStatus())
                .isEqualTo("Unsuccessful");
    }
}
