package com.gotinite.course_management.controllers;

import com.gotinite.course_management.dtos.TeacherDto;
import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Grade;
import com.gotinite.course_management.models.Teacher;
import com.gotinite.course_management.services.CourseService;
import com.gotinite.course_management.services.GradeService;
import com.gotinite.course_management.services.TeacherService;
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
public class TeacherController {

    private final GradeService gradeService;
    private final CourseService courseService;
    private final TeacherService teacherService;

    @GetMapping("/page/teachers")
    public ResponseEntity<Map<String, Object>> fetchTeachers(
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "10") int perPage
    ) {
        Pageable pageable = PageRequest.of(currentPage - 1, perPage);
        Page<Teacher> page = teacherService.getAllTeachers(pageable);
        Map<String, Object> response =Map.of(
                "teachers", page.getContent(),
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/teacher/search")
    public ResponseEntity<List<Teacher>> searchStudents(
            @RequestParam String value,
            @RequestParam String type
    ) {
        List<Teacher> teachers;

        switch (type.toLowerCase()) {
            case "firstname":
                teachers = teacherService.getTeachersByFirstName(value);
                break;
            case "lastname":
                teachers = teacherService.getTeachersByLastName(value);
                break;
            case "fullname":
                teachers = teacherService.getTeachersByFullName(value);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(teachers, HttpStatus.OK);
    }

    @GetMapping("/teacher/courses")
    public ResponseEntity<List<Course>> getTeacherCourses(@RequestParam String email) {
        try {
            List<Course> courses = courseService.getCoursesByTeacher(email);
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/teacher/grades")
    public ResponseEntity<List<Grade>> getTeacherGrades(@RequestParam String email) {
        try {
            List<Grade> grades = gradeService.getByTeacher(email);
            return new ResponseEntity<>(grades, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/teacher-email")
    public ResponseEntity<?> updateTeacherEmail(
            @RequestBody TeacherDto dto,
            @RequestParam String email ) {
        try {
            teacherService.updateTeacherEmail(email, dto);
            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(
                    Map.of("error", exception.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/grade/{id}")
    public ResponseEntity<String> updateGrade(@PathVariable Long id, @RequestBody Double value) {
        try {
            gradeService.updateValueById(value, id);
            return ResponseEntity.ok("Grade updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/create/teacher")
    public ResponseEntity<?> createStudent(@RequestBody TeacherDto dto) {
        try {
            Teacher teacher = teacherService.createTeacher(dto);
            return new ResponseEntity<>(teacher, HttpStatus.CREATED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(
                    Map.of("error", exception.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add/grade")
    public ResponseEntity<?> addGrade(
            @RequestParam String studentEmail,
            @RequestParam String courseName,
            @RequestParam String  teacherEmail,
            @RequestParam Double value
    ) {
        try {
            Grade newGrade = gradeService.addGrade(studentEmail, courseName, teacherEmail, value);
            return ResponseEntity.status(HttpStatus.CREATED).body(newGrade);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
