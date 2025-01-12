package com.gotinite.course_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotinite.course_management.dtos.CourseDto;
import com.gotinite.course_management.models.*;
import com.gotinite.course_management.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CourseController.class)
public class CourseControllerTest {

    @MockBean
    private CourseService courseService;

    @MockBean
    private GradeService gradeService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private EnrollmentService enrollmentService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fetchCoursesShouldReturnPagedCourses() throws Exception {
        when(courseService.getAllCourses(any())).thenReturn(getCoursePage());

        mockMvc.perform(get("/page/courses")
                        .param("currentPage", "1")
                        .param("perPage", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.courses[0].id").value(1));
    }

    @Test
    void getCoursesByStatusShouldReturnCourses() throws Exception {
        when(courseService.getCoursesByStatus(eq("ACTIVE"))).thenReturn(getCoursesList());
        mockMvc.perform(get("/course/by-status")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void getCourseEnrollmentsShouldReturnEnrollments() throws Exception {
        when(enrollmentService.getEnrollmentsByCourse("Math")).thenReturn(getEnrollments());
        mockMvc.perform(get("/course/enrollments")
                        .param("name", "Math"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Successful"))
                .andExpect(jsonPath("$[1].status").value("Successful"))
                .andExpect(jsonPath("$[0].student.firstName").value("Ivan"))
                .andExpect(jsonPath("$[1].student.firstName").value("Anton"));
    }

    @Test
    void getCourseStudentsShouldReturnStudents() throws Exception {
        when(studentService.getStudentsByCourse("Math")).thenReturn(getStudents());
        mockMvc.perform(get("/course/students")
                        .param("name", "Math"))
                .andExpect(jsonPath("$[0].firstName").value("Ivan"))
                .andExpect(jsonPath("$[1].firstName").value("Anton"));
    }

    @Test
    void getCourseGradesShouldReturnGrades() throws Exception {
        when(gradeService.getByCourse("Math")).thenReturn(getGrades());
        mockMvc.perform(get("/course/grades")
                        .param("name", "Math"))
                .andExpect(jsonPath("$[0].value").value(5.2))
                .andExpect(jsonPath("$[1].value").value(4.7));
    }

    @Test
    void getCourseTeacherShouldReturnTeacher() throws Exception {
        when(teacherService.getTeacherByCourse("Math")).thenReturn(Optional.of(getTeacher()));
        mockMvc.perform(get("/course/teacher")
                        .param("name", "Math"))
                .andExpect(jsonPath("$.firstName").value("Hristo"))
                .andExpect(jsonPath("$.lastName").value("Hristov"));
        verify(teacherService).getTeacherByCourse(eq("Math"));
    }

    @Test
    void updateCourseStatusShouldReturnAccepted() throws Exception {
        String status = "ACTIVE";
        CourseDto courseDto = new CourseDto("Math", "Pending");
        doNothing().when(courseService).updateCourseStatus(status, courseDto);
        mockMvc.perform(put("/update/course-status")
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(courseDto)))
                .andExpect(status().isAccepted());
        verify(courseService).updateCourseStatus(eq(status), eq(courseDto));
    }

    @Test
    void updateCourseNameShouldReturnAccepted() throws Exception {
        String name = "Math Advanced";
        CourseDto courseDto = new CourseDto("Math", "Active");
        doNothing().when(courseService).updateCourseName(name, courseDto);
        mockMvc.perform(put("/update/course-name")
                        .param("name", name)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(courseDto)))
                .andExpect(status().isAccepted());
        verify(courseService).updateCourseName(eq(name), eq(courseDto));
    }

    @Test
    void createCourseShouldReturnCreatedCourse() throws Exception {
        CourseDto dto = new CourseDto("Math", "ACTIVE");
        when(courseService.createCourse(dto)).thenReturn(getMockedCourse());
        mockMvc.perform(post("/create/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Math"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
        verify(courseService, times(1)).createCourse(dto);
    }

    @Test
    void assignCourseToTeacherShouldReturnAssignedCourse() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Hristo");
        teacher.setLastName("Hristov");
        teacher.setEmail("hristo.hristov@example.com");
        Course course = getMockedCourse();
        course.setTeacher(teacher);
        when(courseService.assignCourseToTeacher("Math", teacher.getEmail())).thenReturn(course);
        mockMvc.perform(post("/course/assign-to-teacher")
                        .param("courseName", "Math")
                        .param("teacherEmail", teacher.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Math"))
                .andExpect(jsonPath("$.teacher.firstName").value("Hristo"))
                .andExpect(jsonPath("$.teacher.lastName").value("Hristov"))
                .andExpect(jsonPath("$.teacher.email").value(teacher.getEmail()));
        verify(courseService, times(1))
                .assignCourseToTeacher("Math", teacher.getEmail());
    }

    private Course getMockedCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setName("Math");
        course.setStatus("ACTIVE");
        return course;
    }

    private List<Course> getCoursesList() {
        return List.of(getMockedCourse());
    }

    private Page<Course> getCoursePage() {
        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<>(List.of(getMockedCourse()), pageable, 1L);
    }

    private List<Enrollment> getEnrollments() {
        Course course = getMockedCourse();
        Student firstStudent = new Student();
        firstStudent.setFirstName("Ivan");
        Student secondStudent = new Student();
        secondStudent.setFirstName("Anton");
        return Arrays.asList(
                new Enrollment("Successful", course, firstStudent),
                new Enrollment("Successful", course, secondStudent)
        );
    }

    private List<Student> getStudents() {
        Course course = getMockedCourse();
        Student firstStudent = new Student();
        firstStudent.setFirstName("Ivan");
        firstStudent.setCourses(new HashSet<>());
        Student secondStudent = new Student();
        secondStudent.setFirstName("Anton");
        secondStudent.setCourses(new HashSet<>());
        firstStudent.getCourses().add(course);
        secondStudent.getCourses().add(course);
        List<Student> students = new ArrayList<>();
        students.add(firstStudent);
        students.add(secondStudent);
        return  students;
    }

    private List<Grade> getGrades() {
        Course course = getMockedCourse();
        course.setGrades(new HashSet<>());
        Grade firstGrade = new Grade();
        firstGrade.setValue(5.2);
        Grade secondGrade = new Grade();
        secondGrade.setValue(4.7);
        course.getGrades().add(firstGrade);
        course.getGrades().add(secondGrade);
        return Arrays.asList(
                firstGrade,
                secondGrade
        );
    }

    private Teacher getTeacher() {
        Course course = getMockedCourse();
        Teacher teacher = new Teacher();
        teacher.setFirstName("Hristo");
        teacher.setLastName("Hristov");
        teacher.setCourses(new HashSet<>());
        teacher.getCourses().add(course);
        return teacher;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
