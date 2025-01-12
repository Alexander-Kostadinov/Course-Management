package com.gotinite.course_management.services;

import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Grade;
import com.gotinite.course_management.models.Student;
import com.gotinite.course_management.models.Teacher;
import com.gotinite.course_management.repositories.CourseRepository;
import com.gotinite.course_management.repositories.GradeRepository;
import com.gotinite.course_management.repositories.StudentRepository;
import com.gotinite.course_management.repositories.TeacherRepository;
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
public class GradeService {

    private final GradeRepository gradeRepository;
    private final CourseRepository courseRepository;
    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public List<Grade> getByStudent(String email) {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            Long id = student.get().getId();
            return gradeRepository.findByStudentId(id);
        } else {
            throw new IllegalArgumentException("Student not found!");
        }
    }

    public List<Grade> getByCourse(String name) {
        Optional<Course> course = courseRepository.findByName(name);
        if (course.isPresent()) {
            Long id = course.get().getId();
            return gradeRepository.findByCourseId(id);
        } else {
            throw new IllegalArgumentException("Course not found!");
        }
    }

    public List<Grade> getByTeacher(String email) {
        Optional<Teacher> teacher = teacherRepository.findByEmail(email);
        if (teacher.isPresent()) {
            Long id = teacher.get().getId();
            return gradeRepository.findByTeacherId(id);
        } else {
            throw new IllegalArgumentException("Teacher not found!");
        }
    }

    public Optional<Grade> getByStudentAndCourse(String email, String name) {
        Optional<Student> dbObjStudent = studentRepository.findByEmail(email);
        Optional<Course> dbObjCourse = courseRepository.findByName(name);
        Student student = dbObjStudent.orElseThrow(() ->
                new IllegalArgumentException("Student not found!"));
        Course course = dbObjCourse.orElseThrow(() ->
                new IllegalArgumentException("Course not found!"));
        Long studentId = student.getId();
        Long courseId = course.getId();
        return gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Transactional
    public void updateValueById(Double value, Long id) {
        if (value < 2 || value > 6) {
            throw new IllegalArgumentException("Incorrect value for grade!");
        } else if (!gradeRepository.existsById(id)) {
            throw new IllegalArgumentException("Grade not found!");
        }
        gradeRepository.updateValueById(value, id);
    }

    @Transactional
    public Grade addGrade(String studentEmail, String courseName,
                          String teacherEmail, Double value) {
        Optional<Student> dbObjStudent = studentRepository.findByEmail(studentEmail);
        Optional<Course> dbObjCourse = courseRepository.findByName(courseName);
        Optional<Teacher> dbObjTeacher = teacherRepository.findByEmail(teacherEmail);
        Student student = dbObjStudent.orElseThrow(() ->
                new IllegalArgumentException("Student not found!"));
        Course course = dbObjCourse.orElseThrow(() ->
                new IllegalArgumentException("Course not found!"));
        Teacher teacher = dbObjTeacher.orElseThrow(() ->
                new IllegalArgumentException("Teacher not found!"));
        List<Student> courseStudents = studentService.getStudentsByCourse(courseName);

        if (value < 2 || value > 6) {
            throw new IllegalArgumentException("Incorrect value for grade!");
        } else if (gradeRepository.existsByStudentAndCourse(student, course)) {
            throw new IllegalArgumentException("The student already has a grade for this course!");
        } else if (!teacher.equals(course.getTeacher())) {
            throw new IllegalArgumentException("The teacher is not allowed to grade this course!");
        } else if (!courseStudents.contains(student)) {
            throw new IllegalArgumentException("The student is not enrolled in this course!");
        }

        Grade grade = new Grade(value, student, course, teacher);
        student.getGrades().add(grade);
        course.getGrades().add(grade);
        teacher.getGrades().add(grade);
        return gradeRepository.saveAndFlush(grade);
    }
}
