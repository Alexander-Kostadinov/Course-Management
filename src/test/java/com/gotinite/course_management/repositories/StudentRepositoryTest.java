package com.gotinite.course_management.repositories;

import com.gotinite.course_management.models.Student;
import jakarta.persistence.EntityManager;
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
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testExistsByEmail() {
        assertThat(studentRepository.existsByEmail("angel.angelov@example.com"))
                .isEqualTo(false);
        assertThat(studentRepository.existsByEmail("ivan.ivanov@example.com"))
                .isEqualTo(true);
    }

    @Test
    void testFindByEmail() {
        assertThat(studentRepository.findByEmail("petar.petrov@example.com").orElseThrow())
                .isNotNull()
                .isEqualTo(studentRepository.findAll().get(1));
    }

    @Test
    void testFindByFirstName() {
        assertThat(studentRepository.findByFirstName("Ivan").getFirst())
                .isNotNull()
                .isEqualTo(studentRepository.findAll().getFirst());
    }

    @Test
    void testFindByLastName() {
        assertThat(studentRepository.findByLastName("Ivanov").getFirst())
                .isNotNull()
                .isEqualTo(studentRepository.findAll().getFirst());
    }

    @Test
    void testFindByFullName() {
        assertThat(studentRepository.findByFullName("Petar Petrov").getFirst())
                .isNotNull()
                .isEqualTo(studentRepository.findAll().get(1));
    }

    @Test
    void testFindByCourseId() {
        List<Student> students = studentRepository.findByCourseId(2L);
        assertThat(students.getFirst().getId())
                .isNotNull()
                .isEqualTo(1);
        assertThat(students.getLast().getId())
                .isNotNull()
                .isEqualTo(2);
    }

    @Test
    void testUpdateEmailById() {
        Long id = studentRepository.findAll().getFirst().getId();
        studentRepository.updateEmailById(id, "test@example.com");
        studentRepository.flush();
        entityManager.clear();
        assertThat(studentRepository.findById(id).orElseThrow().getEmail())
                .isNotNull()
                .isEqualTo("test@example.com");
    }
}
