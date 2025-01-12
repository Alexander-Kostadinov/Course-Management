package com.gotinite.course_management.services;

import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Enrollment;
import com.gotinite.course_management.models.Student;
import com.gotinite.course_management.repositories.CourseRepository;
import com.gotinite.course_management.repositories.EnrollmentRepository;
import com.gotinite.course_management.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public List<Enrollment> getEnrollmentsByStatus(String status, String email) {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            Long id = student.get().getId();
            return enrollmentRepository.findByStatus(id, status);
        } else {
            throw new IllegalArgumentException("Student not found!");
        }
    }

    public List<Enrollment> getEnrollmentsByCourse(String name) {
        Optional<Course> course = courseRepository.findByName(name);
        if (course.isPresent()) {
            Long id = course.get().getId();
            return enrollmentRepository.findEnrollmentsByCourseId(id);
        } else {
            throw new IllegalArgumentException("Course not found!");
        }
    }

    public List<Enrollment> getEnrollmentsByStudent(String email) {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            Long id = student.get().getId();
            return enrollmentRepository.findEnrollmentsByStudentId(id);
        } else {
            throw new IllegalArgumentException("Student not found!");
        }
    }

    @Transactional
    public Enrollment enrollStudentToCourse(String courseName, String studentEmail) {
        Optional<Course> dbObjCourse = courseRepository.findByName(courseName);
        Optional<Student> dbObjStudent = studentRepository.findByEmail(studentEmail);
        Course course = dbObjCourse.orElseThrow(() ->
                new IllegalArgumentException("Course not found!"));
        Student student = dbObjStudent.orElseThrow(() ->
                new IllegalArgumentException("Student not found!"));

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new IllegalStateException("Student is already enrolled in this course!");
        }

        Enrollment enrollment = new Enrollment("Completed", course, student);
        course.getEnrollments().add(enrollment);
        student.getEnrollments().add(enrollment);
        course.getStudents().add(student);
        student.getCourses().add(course);
        return enrollmentRepository.save(enrollment);
    }
}
