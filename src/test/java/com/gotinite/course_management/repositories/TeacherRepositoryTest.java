package com.gotinite.course_management.repositories;

import com.gotinite.course_management.models.Teacher;
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
class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testExistsByEmail() {
        assertThat(teacherRepository.existsByEmail("angel.angelov@example.com"))
                .isEqualTo(true);
        assertThat(teacherRepository.existsByEmail("ivan.ivanov@example.com"))
                .isEqualTo(false);
    }

    @Test
    void testFindByEmail() {
        assertThat(teacherRepository.findByEmail("alex.aleksandrov@example.com").orElseThrow())
                .isNotNull()
                .isEqualTo(teacherRepository.findAll().get(1));
    }

    @Test
    void testFindByFirstName() {
        assertThat(teacherRepository.findByFirstName("Angel").getFirst())
                .isNotNull()
                .isEqualTo(teacherRepository.findAll().getFirst());
    }

    @Test
    void testFindByLastName() {
        assertThat(teacherRepository.findByLastName("Angelov").getFirst())
                .isNotNull()
                .isEqualTo(teacherRepository.findAll().getFirst());
    }

    @Test
    void testFindByFullName() {
        assertThat(teacherRepository.findByFullName("Alex Aleksandrov").getFirst())
                .isNotNull()
                .isEqualTo(teacherRepository.findAll().get(1));
    }

    @Test
    void testFindByCourseId() {
        Teacher firstTeacher = teacherRepository.findByCourseId(1L).orElseThrow();
        Teacher secondTeacher = teacherRepository.findByCourseId(3L).orElseThrow();
        assertThat(firstTeacher.getFirstName())
                .isNotNull()
                .isEqualTo("Angel");
        assertThat(secondTeacher.getFirstName())
                .isNotNull()
                .isEqualTo("Alex");
    }

    @Test
    void testUpdateEmailById() {
        Long id = teacherRepository.findAll().getFirst().getId();
        teacherRepository.updateEmailById("test@example.com", id);
        teacherRepository.flush();
        entityManager.clear();
        assertThat(teacherRepository.findById(id).orElseThrow().getEmail())
                .isNotNull()
                .isEqualTo("test@example.com");
    }
}
