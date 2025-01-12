package com.gotinite.course_management.controllers;

import com.gotinite.course_management.dtos.CourseDto;
import com.gotinite.course_management.models.*;
import com.gotinite.course_management.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CourseController {

    private final GradeService gradeService;
    private final CourseService courseService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/page/courses")
    public ResponseEntity<Map<String, Object>> fetchCourses(
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "10") int perPage
    ) {
        Pageable pageable = PageRequest.of(currentPage - 1, perPage);
        Page<Course> page = courseService.getAllCourses(pageable);
        Map<String, Object> response = Map.of(
                "courses", page.getContent(),
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/course/by-status")
    public ResponseEntity<List<Course>> getCoursesByStatus(@RequestParam String status) {
        try {
            List<Course> courses = courseService.getCoursesByStatus(status);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/course/enrollments")
    public ResponseEntity<List<Enrollment>> getEnrollments(@RequestParam String name) {
        try {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(name);
            return new ResponseEntity<>(enrollments, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/course/students")
    public ResponseEntity<List<Student>> getStudents(@RequestParam String name) {
        try {
            List<Student> students = studentService.getStudentsByCourse(name);
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/course/grades")
    public ResponseEntity<List<Grade>> getGrades(@RequestParam String name) {
        try {
            List<Grade> grades = gradeService.getByCourse(name);
            return new ResponseEntity<>(grades, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/course/teacher")
    public ResponseEntity<Teacher> getTeacher(@RequestParam String name) {
        try {
            Teacher teacher = teacherService.getTeacherByCourse(name)
                    .orElseThrow(() -> new IllegalArgumentException("Teacher not found!"));
            return new ResponseEntity<>(teacher, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/course-status")
    public ResponseEntity<?> updateCourseStatus(
            @RequestParam String status,
            @RequestBody CourseDto dto
    ) {
        try {
            courseService.updateCourseStatus(status, dto);
            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(
                    Map.of("error", exception.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/course-name")
    public ResponseEntity<?> updateCourseName(
            @RequestParam String name,
            @RequestBody CourseDto dto
    ) {
        try {
            courseService.updateCourseName(name, dto);
            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(
                    Map.of("error", exception.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create/course")
    public ResponseEntity<?> createCourse(@RequestBody CourseDto dto) {
        try {
            Course course = courseService.createCourse(dto);
            return new ResponseEntity<>(course, HttpStatus.CREATED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(
                    Map.of("error", exception.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/course/assign-to-teacher")
    public ResponseEntity<?> assignToTeacher(
            @RequestParam String courseName,
            @RequestParam String teacherEmail
    ) {
        try {
            Course assignedCourse = courseService.assignCourseToTeacher(courseName, teacherEmail);
            return ResponseEntity.status(HttpStatus.OK).body(assignedCourse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
