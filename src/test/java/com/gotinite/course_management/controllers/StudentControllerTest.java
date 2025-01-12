package com.gotinite.course_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gotinite.course_management.dtos.StudentDto;
import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Enrollment;
import com.gotinite.course_management.models.Grade;
import com.gotinite.course_management.models.Student;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentController.class)
public class StudentControllerTest {

    @MockBean
    private CourseService courseService;

    @MockBean
    private GradeService gradeService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private EnrollmentService enrollmentService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fetchCoursesShouldReturnPagedCourses() throws Exception {
        when(studentService.getAllStudents(any())).thenReturn(getStudentPage());
        mockMvc.perform(get("/page/students")
                        .param("currentPage", "1")
                        .param("perPage", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.students[0].id").value(1));
    }

    @Test
    void searchStudentsByFirstNameShouldReturnStudents() throws Exception {
        when(studentService.getStudentByFirstName("Ivan")).thenReturn(getStudentList());
        mockMvc.perform(get("/student/search")
                        .param("value", "Ivan")
                        .param("type", "firstname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Ivan"))
                .andExpect(jsonPath("$[0].lastName").value("Ivanov"));
        verify(studentService, times(1)).getStudentByFirstName("Ivan");
    }

    @Test
    void searchStudentsByLastNameShouldReturnStudents() throws Exception {
        when(studentService.getStudentByLastName("Ivanov")).thenReturn(getStudentList());
        mockMvc.perform(get("/student/search")
                        .param("value", "Ivanov")
                        .param("type", "lastname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Ivan"))
                .andExpect(jsonPath("$[0].lastName").value("Ivanov"));
        verify(studentService, times(1)).getStudentByLastName("Ivanov");
    }

    @Test
    void searchStudentsByFullNameShouldReturnStudents() throws Exception {
        when(studentService.getStudentByFullName("Ivan Ivanov")).thenReturn(getStudentList());
        mockMvc.perform(get("/student/search")
                        .param("value", "Ivan Ivanov")
                        .param("type", "fullname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Ivan"))
                .andExpect(jsonPath("$[0].lastName").value("Ivanov"));
        verify(studentService, times(1)).getStudentByFullName("Ivan Ivanov");
    }

    @Test
    void getStudentEnrollmentsWithoutStatusShouldReturnEnrollments() throws Exception {
        when(enrollmentService.getEnrollmentsByStudent("ivan.ivanov@example.com"))
                .thenReturn(getEnrollments());
        mockMvc.perform(get("/student/enrollments")
                        .param("email", "ivan.ivanov@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Successful"))
                .andExpect(jsonPath("$[1].status").value("Unsuccessful"))
                .andExpect(jsonPath("$[0].student.email").value("ivan.ivanov@example.com"))
                .andExpect(jsonPath("$[0].course.name").value("Math"))
                .andExpect(jsonPath("$[1].course.name").value("Java"));
        verify(enrollmentService, times(1))
                .getEnrollmentsByStudent("ivan.ivanov@example.com");
    }

    @Test
    void getStudentEnrollmentsWithStatusShouldReturnEnrollments() throws Exception {
        when(enrollmentService
                .getEnrollmentsByStatus("Successful", "ivan.ivanov@example.com"))
                .thenReturn(getEnrollments());
        mockMvc.perform(get("/student/enrollments")
                        .param("email", "ivan.ivanov@example.com")
                        .param("status", "Successful"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].student.email").value("ivan.ivanov@example.com"))
                .andExpect(jsonPath("$[0].status").value("Successful"))
                .andExpect(jsonPath("$[0].course.name").value("Math"));
        verify(enrollmentService, times(1))
                .getEnrollmentsByStatus("Successful", "ivan.ivanov@example.com");
    }

    @Test
    void getStudentCoursesShouldReturnCourses() throws Exception {
        Student student = getMockedStudent();
        String email = student.getEmail();
        List<Course> courses = getCourses();
        courses.getFirst().getStudents().add(student);
        courses.getLast().getStudents().add(student);
        when(courseService.getCoursesByStudent(email)).thenReturn(courses);
        mockMvc.perform(get("/student/courses")
                        .param("email", email))
                .andExpect(jsonPath("$[0].name").value("Math"))
                .andExpect(jsonPath("$[1].name").value("Java"))
                .andExpect(jsonPath("$[0].students[0].firstName").value("Ivan"))
                .andExpect(jsonPath("$[1].students[0].firstName").value("Ivan"));
        verify(courseService, times(1)).getCoursesByStudent(email);
    }

    @Test
    void getStudentGradesWithoutCourseShouldReturnGrades() throws Exception {
        Student student = getMockedStudent();
        String email = student.getEmail();
        List<Grade> grades = getGrades();
        grades.getFirst().setStudent(student);
        grades.getLast().setStudent(student);
        when(gradeService.getByStudent(email)).thenReturn(grades);
        mockMvc.perform(get("/student/grades")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].student.id").value(1L))
                .andExpect(jsonPath("$[1].student.id").value(1L))
                .andExpect(jsonPath("$[0].value").value(5.2))
                .andExpect(jsonPath("$[1].value").value(4.7));
        verify(gradeService, times(1)).getByStudent(email);
    }

    @Test
    void getStudentGradeForCourseShouldReturnGrade() throws Exception {
        Student student = getMockedStudent();
        String email = student.getEmail();
        Grade grade = getGrades().getFirst();
        Course course = getCourses().getFirst();
        grade.setStudent(student);
        grade.setCourse(course);
        when(gradeService.getByStudentAndCourse(email, course.getName())).thenReturn(Optional.of(grade));
        mockMvc.perform(get("/student/grades")
                        .param("email", email)
                        .param("courseName", course.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(5.2))
                .andExpect(jsonPath("$.student.id").value(1L))
                .andExpect(jsonPath("$.course.name").value("Math"));
        verify(gradeService, times(1))
                .getByStudentAndCourse(email, course.getName());
    }

    @Test
    void updateStudentEmailShouldReturnAccepted() throws Exception {
        StudentDto dto = new StudentDto(
                "Ivan", "Ivanov", "ivan.ivanov@example.com");
        String newEmail = "test@example.com";
        doNothing().when(studentService).updateStudentEmail(dto, newEmail);
        mockMvc.perform(put("/update/student-email")
                        .param("email", newEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isAccepted());
        verify(studentService, times(1))
                .updateStudentEmail(eq(dto), eq(newEmail));
    }

    @Test
    void createStudentShouldReturnCreatedStudent() throws Exception {
        StudentDto dto = new StudentDto(
                "Ivan", "Ivanov", "ivan.ivanov@example.com");
        Student student = getMockedStudent();
        when(studentService.createStudent(dto)).thenReturn(student);
        mockMvc.perform(post("/create/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Ivan"))
                .andExpect(jsonPath("$.lastName").value("Ivanov"))
                .andExpect(jsonPath("$.email").value("ivan.ivanov@example.com"));
        verify(studentService, times(1)).createStudent(dto);
    }

    @Test
    void enrollStudentToCourseShouldReturnEnrollment() throws Exception {
        Student student = getMockedStudent();
        Course course = new Course();
        course.setName("Physics");
        Enrollment enrollment = new Enrollment("Successful", course, student);

        when(enrollmentService.enrollStudentToCourse(
                course.getName(), student.getEmail())).thenReturn(enrollment);
        mockMvc.perform(post("/student/enroll-to-course")
                        .param("courseName", course.getName())
                        .param("studentEmail", student.getEmail()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.course.name").value("Physics"))
                .andExpect(jsonPath("$.student.email").value(student.getEmail()));
        verify(enrollmentService, times(1))
                .enrollStudentToCourse(course.getName(), student.getEmail());
    }

    private Student getMockedStudent() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Ivan");
        student.setLastName("Ivanov");
        student.setEmail("ivan.ivanov@example.com");
        student.setGrades(new HashSet<>());
        return  student;
    }

    private List<Student> getStudentList() {
        return List.of(getMockedStudent());
    }

    private Page<Student> getStudentPage() {
        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<>(List.of(getMockedStudent()), pageable, 1L);
    }

    private List<Course> getCourses() {
        Course mathCourse = new Course();
        mathCourse.setName("Math");
        mathCourse.setStatus("Active");
        mathCourse.setStudents(new HashSet<>());
        Course javaCourse = new Course();
        javaCourse.setName("Java");
        javaCourse.setStatus("Pending");
        javaCourse.setStudents(new HashSet<>());
        return Arrays.asList(
                mathCourse,
                javaCourse
        );
    }

    private List<Enrollment> getEnrollments() {
        List<Course> courses = getCourses();
        Student student = getMockedStudent();
        return Arrays.asList(
                new Enrollment("Successful", courses.getFirst(), student),
                new Enrollment("Unsuccessful", courses.getLast(), student)
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
