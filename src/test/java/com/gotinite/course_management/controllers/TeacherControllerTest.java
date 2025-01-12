package com.gotinite.course_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotinite.course_management.dtos.TeacherDto;
import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Grade;
import com.gotinite.course_management.models.Student;
import com.gotinite.course_management.models.Teacher;
import com.gotinite.course_management.services.CourseService;
import com.gotinite.course_management.services.GradeService;
import com.gotinite.course_management.services.TeacherService;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TeacherController.class)
public class TeacherControllerTest {

    @MockBean
    private CourseService courseService;

    @MockBean
    private GradeService gradeService;

    @MockBean
    private TeacherService teacherService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fetchCoursesShouldReturnPagedCourses() throws Exception {
        when(teacherService.getAllTeachers(any())).thenReturn(getTeacherPage());
        mockMvc.perform(get("/page/teachers")
                        .param("currentPage", "1")
                        .param("perPage", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.teachers[0].id").value(1));
    }

    @Test
    void searchTeachersByFirstNameShouldReturnTeachers() throws Exception {
        when(teacherService.getTeachersByFirstName("Angel")).thenReturn(getTeacherList());
        mockMvc.perform(get("/teacher/search")
                        .param("value", "Angel")
                        .param("type", "firstname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Angel"))
                .andExpect(jsonPath("$[0].lastName").value("Angelov"));
        verify(teacherService, times(1)).getTeachersByFirstName("Angel");
    }

    @Test
    void searchTeachersByLastNameShouldReturnTeachers() throws Exception {
        when(teacherService.getTeachersByLastName("Angelov")).thenReturn(getTeacherList());
        mockMvc.perform(get("/teacher/search")
                        .param("value", "Angelov")
                        .param("type", "lastname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Angel"))
                .andExpect(jsonPath("$[0].lastName").value("Angelov"));
        verify(teacherService, times(1)).getTeachersByLastName("Angelov");
    }

    @Test
    void searchTeachersByFullNameShouldReturnTeachers() throws Exception {
        when(teacherService.getTeachersByFullName("Angel Angelov")).thenReturn(getTeacherList());
        mockMvc.perform(get("/teacher/search")
                        .param("value", "Angel Angelov")
                        .param("type", "fullname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Angel"))
                .andExpect(jsonPath("$[0].lastName").value("Angelov"));
        verify(teacherService, times(1)).getTeachersByFullName("Angel Angelov");
    }

    @Test
    void getTeacherCoursesShouldReturnCourses() throws Exception {
        Teacher teacher = getMockedTeacher();
        String email = teacher.getEmail();
        List<Course> courses = getCourses();
        courses.getFirst().setTeacher(teacher);
        courses.getLast().setTeacher(teacher);
        when(courseService.getCoursesByTeacher(email)).thenReturn(courses);
        mockMvc.perform(get("/teacher/courses")
                        .param("email", email))
                .andExpect(jsonPath("$[0].name").value("Math"))
                .andExpect(jsonPath("$[1].name").value("Java"))
                .andExpect(jsonPath("$[0].teacher.firstName").value("Angel"))
                .andExpect(jsonPath("$[1].teacher.firstName").value("Angel"));
        verify(courseService, times(1)).getCoursesByTeacher(email);
    }

    @Test
    void getTeacherGradesShouldReturnGrades() throws Exception {
        String email = getMockedTeacher().getEmail();
        when(gradeService.getByTeacher(email)).thenReturn(getGrades());
        mockMvc.perform(get("/teacher/grades")
                        .param("email", email))
                .andExpect(jsonPath("$[0].value").value(5.2))
                .andExpect(jsonPath("$[1].value").value(4.7));
        verify(gradeService, times(1)).getByTeacher(email);
    }

    @Test
    void updateTeacherEmailShouldReturnAccepted() throws Exception {
        String newEmail = "newteacher@example.com";
        TeacherDto dto = new TeacherDto(
                "Angel", "Angelov", "angel.angelov@example.com");
        Teacher teacher = getMockedTeacher();
        teacher.setEmail(newEmail);
        doNothing().when(teacherService).updateTeacherEmail(eq(teacher.getEmail()), eq(dto));
        mockMvc.perform(put("/update/teacher-email")
                        .param("email", newEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isAccepted());
        verify(teacherService, times(1))
                .updateTeacherEmail(eq(newEmail), eq(dto));
    }

    @Test
    void updateGradeShouldReturnSuccess() throws Exception {
        Long gradeId = 1L;
        Double newGradeValue = 5.5;
        doNothing().when(gradeService).updateValueById(newGradeValue, gradeId);
        mockMvc.perform(put("/update/grade/{id}", gradeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newGradeValue)))
                .andExpect(status().isOk())
                .andExpect(content().string("Grade updated successfully."));
        verify(gradeService, times(1)).updateValueById(newGradeValue, gradeId);
    }

    @Test
    void createTeacherShouldReturnCreatedTeacher() throws Exception {
        TeacherDto dto = new TeacherDto(
                "Angel", "Angelov", "angel.angelov@example.com");
        Teacher teacher = getMockedTeacher();
        when(teacherService.createTeacher(dto)).thenReturn(teacher);
        mockMvc.perform(post("/create/teacher")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Angel"))
                .andExpect(jsonPath("$.lastName").value("Angelov"))
                .andExpect(jsonPath("$.email").value("angel.angelov@example.com"));
        verify(teacherService, times(1)).createTeacher(dto);
    }

    @Test
    void addGradeShouldReturnCreatedGrade() throws Exception {
        Teacher teacher = getMockedTeacher();
        Student student = new Student();
        student.setFirstName("Ivan");
        student.setEmail("ivan.ivanov@example.com");
        Course course = getCourses().getFirst();
        Grade newGrade = getGrades().getFirst();
        newGrade.setValue(5.5);
        newGrade.setStudent(student);
        newGrade.setTeacher(teacher);
        newGrade.setCourse(course);

        when(gradeService.addGrade(student.getEmail(), course.getName(), teacher.getEmail(), newGrade.getValue()))
                .thenReturn(newGrade);
        mockMvc.perform(post("/add/grade")
                        .param("studentEmail", student.getEmail())
                        .param("courseName", course.getName())
                        .param("teacherEmail", teacher.getEmail())
                        .param("value", newGrade.getValue().toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value(5.5))
                .andExpect(jsonPath("$.student.email").value("ivan.ivanov@example.com"))
                .andExpect(jsonPath("$.course.name").value("Math"))
                .andExpect(jsonPath("$.teacher.email").value("angel.angelov@example.com"));
        verify(gradeService, times(1))
                .addGrade(student.getEmail(), course.getName(), teacher.getEmail(), newGrade.getValue());
    }

    private Teacher getMockedTeacher() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Angel");
        teacher.setLastName("Angelov");
        teacher.setEmail("angel.angelov@example.com");
        teacher.setCourses(new HashSet<>());
        teacher.setGrades(new HashSet<>());
        return teacher;
    }

    private List<Teacher> getTeacherList() {
        return List.of(getMockedTeacher());
    }

    private Page<Teacher> getTeacherPage() {
        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<>(List.of(getMockedTeacher()), pageable, 1L);
    }

    private List<Course> getCourses() {
        Course mathCourse = new Course();
        mathCourse.setName("Math");
        mathCourse.setStatus("Active");
        Course javaCourse = new Course();
        javaCourse.setName("Java");
        javaCourse.setStatus("Pending");
        return Arrays.asList(
                mathCourse,
                javaCourse
        );
    }

    private List<Grade> getGrades() {
        Grade firstGrade = new Grade();
        firstGrade.setValue(5.2);
        Grade secondGrade = new Grade();
        secondGrade.setValue(4.7);
        return Arrays.asList(
                firstGrade,
                secondGrade
        );
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
