package com.gotinite.course_management.services;

import com.gotinite.course_management.dtos.StudentDto;
import com.gotinite.course_management.mappers.StudentMapper;
import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Student;
import com.gotinite.course_management.repositories.CourseRepository;
import com.gotinite.course_management.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        log.info("Fetching all students.");
        System.out.println("\u001B[31m" + " " + Thread.currentThread().getName());
        return studentRepository.findAll();
    }

    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public List<Student> getStudentByFirstName(String firstName) {
        return studentRepository.findByFirstName(firstName);
    }

    public List<Student> getStudentByLastName(String lastName) {
        return studentRepository.findByLastName(lastName);
    }

    public List<Student> getStudentByFullName(String fullName) {
        return  studentRepository.findByFullName(fullName);
    }

    public List<Student> getStudentsByCourse(String courseName) {
        Optional<Course> course = courseRepository.findByName(courseName);
        if (course.isPresent()) {
            Long id = course.get().getId();
            return studentRepository.findByCourseId(id);
        } else {
            throw new IllegalArgumentException("Course not found!");
        }
    }

    @Transactional
    public void updateStudentEmail(StudentDto dto, String email) {
        if (email == null) {
            throw new IllegalArgumentException("The email cannot be empty!");
        } else if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("The email cannot be empty!");
        } else if (studentRepository.existsByEmail(email)) {
            throw new IllegalStateException("A student with this email already exists!");
        }

        Optional<Student> student = studentRepository.findByEmail(dto.email());

        if (student.isPresent()) {
            Long id = student.get().getId();
            log.info("Updating email of student with id: {}", id);
            studentRepository.updateEmailById(id, email);
        } else {
            throw new IllegalStateException("Student not found!");
        }
    }

    @Transactional
    public Student createStudent(StudentDto dto) {
        if (dto == null) throw new IllegalArgumentException();
        if (studentRepository.existsByEmail(dto.email())) {
            throw new IllegalStateException("A student with this email already exists!");
        }
        log.info("Creating a new student: {}", dto);
        Student newStudent = studentMapper.convertDtoToEntity(dto);
        return studentRepository.saveAndFlush(newStudent);
    }
}
