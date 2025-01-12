package com.gotinite.course_management.controllers;

import com.gotinite.course_management.dtos.StudentDto;
import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Enrollment;
import com.gotinite.course_management.models.Grade;
import com.gotinite.course_management.models.Student;
import com.gotinite.course_management.services.CourseService;
import com.gotinite.course_management.services.EnrollmentService;
import com.gotinite.course_management.services.GradeService;
import com.gotinite.course_management.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class StudentController {

    private final CourseService courseService;
    private final StudentService studentService;
    private final GradeService gradeService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/page/students")
    public ResponseEntity<Map<String, Object>> fetchStudents(
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "10") int perPage
    ) {
        Pageable pageable = PageRequest.of(currentPage - 1, perPage);
        Page<Student> page = studentService.getAllStudents(pageable);
        Map<String, Object> response =Map.of(
                "students", page.getContent(),
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fetch/async")
    public Map<String, ?> fetchAsync() throws ExecutionException, InterruptedException {
        System.out.println("\u001B[35m" + " " + Thread.currentThread().getName());
        CompletableFuture<List<Student>> studentsFuture = CompletableFuture.supplyAsync(studentService::getAllStudents);
        CompletableFuture<List<Course>> coursesFuture = CompletableFuture.supplyAsync(courseService::getAllCourses);

        return CompletableFuture.allOf(studentsFuture, coursesFuture).thenApply(r ->
                Map.of(
                        "students", studentsFuture.join(),
                        "courses", coursesFuture.join()
                )
        ).get();
    }

    @GetMapping("/student/search")
    public ResponseEntity<List<Student>> searchStudents(
            @RequestParam String value,
            @RequestParam String type
    ) {
        List<Student> students;

        switch (type.toLowerCase()) {
            case "firstname":
                students = studentService.getStudentByFirstName(value);
                break;
            case "lastname":
                students = studentService.getStudentByLastName(value);
                break;
            case "fullname":
                students = studentService.getStudentByFullName(value);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/student/enrollments")
    public ResponseEntity<List<Enrollment>> getStudentEnrollments(
            @RequestParam String email,
            @RequestParam(required = false) String status) {
        try {
            if (status == null || status.isEmpty()) {
                List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(email);
                return new ResponseEntity<>(enrollments, HttpStatus.OK);
            }

            List<Enrollment> enrollmentsByStatus = enrollmentService.getEnrollmentsByStatus(status, email);
            return new ResponseEntity<>(enrollmentsByStatus, HttpStatus.OK);

        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/student/courses")
    public ResponseEntity<List<Course>> getStudentCourses(@RequestParam String email) {
        try {
            List<Course> courses = courseService.getCoursesByStudent(email);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/student/grades")
    public ResponseEntity<?> getStudentGrades(
            @RequestParam String email,
            @RequestParam(required = false)String courseName) {
        try {
            if (courseName != null && !courseName.trim().isEmpty()) {
                Grade grade = gradeService.getByStudentAndCourse(email, courseName)
                        .orElseThrow(() -> new IllegalArgumentException("Grade not found!"));
                return new ResponseEntity<>(grade, HttpStatus.OK);
            }
            List<Grade> grades = gradeService.getByStudent(email);
            return new ResponseEntity<>(grades, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(Map.of("message", exception.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/student-email")
    public ResponseEntity<?> updateStudentEmail(
            @RequestBody StudentDto dto,
            @RequestParam String email ) {
        try {
            studentService.updateStudentEmail(dto, email);
            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(
                    Map.of("error", exception.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create/student")
    public ResponseEntity<?> createStudent(@RequestBody StudentDto dto) {
        try {
            Student student = studentService.createStudent(dto);
            return new ResponseEntity<>(student, HttpStatus.CREATED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(
                    Map.of("error", exception.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/student/enroll-to-course")
    public ResponseEntity<?> enrollStudentToCourse(
            @RequestParam String courseName,
            @RequestParam String studentEmail
    ) {
        try {
            Enrollment enrollment = enrollmentService.enrollStudentToCourse(courseName, studentEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
