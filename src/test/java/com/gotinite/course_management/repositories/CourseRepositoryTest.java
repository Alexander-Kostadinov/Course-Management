package com.gotinite.course_management.repositories;

import com.gotinite.course_management.models.Course;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Sql({
        "/sql/data.sql"
})
class CourseRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void testFindByName() {
        Optional<Course> result = courseRepository.findByName("Math");
        assertThat(result.orElseThrow().getName())
                .isNotNull()
                .isEqualTo("Math");

    }

    @Test
    void testFindByStatus() {
        List<Course> result = courseRepository.findByStatus("ACTIVE");
        assertThat(result.getFirst().getStatus())
                .isNotNull()
                .isEqualTo("ACTIVE");
    }

    @Test
    void testFindByTeacherId() {
        List<Course> result = courseRepository.findByTeacherId(1L);
        assertThat(result.getFirst().getName())
                .isNotNull()
                .isEqualTo("Math");
        assertThat(result.getLast().getName())
                .isNotNull()
                .isEqualTo("Physics");
    }

    @Test
    void testFindByStudentId() {
        List<Course> courses = courseRepository.findByStudentId(1L);
        assertThat(courses.getFirst().getName())
                .isNotNull()
                .isEqualTo("Math");
        assertThat(courses.get(1).getName())
                .isNotNull()
                .isEqualTo("Physics");
    }

    @Test
    void testUpdateNameById() {
        Course course = courseRepository.findAll().get(1);
        Long id = course.getId();
        String name = "Java programming";

        courseRepository.updateNameById(name, id);
        courseRepository.flush();
        entityManager.clear();

        Course updatedCourse = courseRepository.findById(id).orElse(null);
        assertThat(updatedCourse != null ? updatedCourse.getName() : null)
                .isNotNull()
                .isEqualTo(name);
    }

    @Test
    void testUpdateStatusById() {
        Course course = courseRepository.findAll().getFirst();
        courseRepository.updateStatusById("INACTIVE", course.getId());
        courseRepository.flush();
        entityManager.clear();

        Course updatedCourse = courseRepository.findById(course.getId()).orElse(null);
        assertThat(updatedCourse).isNotNull();
        assertThat(updatedCourse.getStatus()).isEqualTo("INACTIVE");
    }
}
