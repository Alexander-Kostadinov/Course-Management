package com.gotinite.course_management.repositories;

import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Grade;
import com.gotinite.course_management.models.Student;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Sql({
        "/sql/data.sql"
})
class GradeRepositoryTest {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private  TeacherRepository teacherRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testExistsByStudentAndCourse() {
        Student student = studentRepository.findAll().getFirst();
        Course course = courseRepository.findAll().getFirst();
        assertThat(gradeRepository.existsByStudentAndCourse(student, course))
                .isEqualTo(true);
        Student secondStudent = studentRepository.findAll().get(1);
        Course secondCourse = courseRepository.findAll().get(1);
        assertThat(gradeRepository.existsByStudentAndCourse(secondStudent, secondCourse))
                .isEqualTo(false);
    }

    @Test
    void testFindByStudentId() {
        Long id = studentRepository.findAll().getFirst().getId();
        assertThat(gradeRepository.findByStudentId(id).getFirst().getValue())
                .isNotNull()
                .isEqualTo(5);
    }

    @Test
    void testFindByCourseId() {
        Long id = courseRepository.findAll().getFirst().getId();
        assertThat(gradeRepository.findByCourseId(id).getFirst().getValue())
                .isNotNull()
                .isEqualTo(5);
    }

    @Test
    void testFindByTeacherId() {
        Long id = teacherRepository.findAll().getFirst().getId();
        assertThat(gradeRepository.findByTeacherId(id).getFirst().getValue())
                .isNotNull()
                .isEqualTo(5);
    }

    @Test
    void testFindByStudentIdAndCourseId() {
        Long studentId = studentRepository.findAll().getFirst().getId();
        Long courseId = courseRepository.findAll().getFirst().getId();
        assertThat(gradeRepository.findByStudentIdAndCourseId(studentId, courseId))
                .isNotNull();
        assertThat(gradeRepository.findByStudentIdAndCourseId(studentId,
                courseId).orElseThrow().getValue())
                .isEqualTo(5);
    }

    @Test
    void testUpdateValueById() {
        Double value = 5.5;
        Long id = 1L;
        gradeRepository.updateValueById(value, id);
        gradeRepository.flush();
        entityManager.clear();

        Grade grade = gradeRepository.findAll().getFirst();
        assertThat(grade.getValue())
                .isEqualTo(value);
    }
}
