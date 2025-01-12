package com.gotinite.course_management.services;

import com.gotinite.course_management.dtos.CourseDto;
import com.gotinite.course_management.mappers.CourseMapper;
import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Student;
import com.gotinite.course_management.models.Teacher;
import com.gotinite.course_management.repositories.CourseRepository;
import com.gotinite.course_management.repositories.StudentRepository;
import com.gotinite.course_management.repositories.TeacherRepository;
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
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    public List<Course> getCoursesByStatus(String status) {
        return courseRepository.findByStatus(status);
    }

    public List<Course> getCoursesByTeacher(String email) {
        Optional<Teacher> teacher = teacherRepository.findByEmail(email);
        if (teacher.isPresent()) {
            Long id = teacher.get().getId();
            return courseRepository.findByTeacherId(id);
        } else {
            throw new IllegalArgumentException("Teacher not found!");
        }
    }

    public List<Course> getCoursesByStudent(String email) {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            Long id = student.get().getId();
            return courseRepository.findByStudentId(id);
        } else {
            throw new IllegalArgumentException("Student not found!");
        }
    }

    @Transactional
    public void updateCourseName(String name, CourseDto dto) {
        if (name == null) {
            throw new IllegalArgumentException("The name cannot be empty!");
        } else if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("The name cannot be empty!");
        } else if (courseRepository.existsByName(name)) {
            throw new IllegalStateException("A course with this name already exists!");
        }

        Optional<Course> course = courseRepository.findByName(dto.name());

        if (course.isPresent()) {
            Long id = course.get().getId();
            log.info("Updating name of course with id: {}", id);
            courseRepository.updateNameById(name, id);
        } else {
            throw new IllegalStateException("Course not found!");
        }
    }

    @Transactional
    public void updateCourseStatus(String status, CourseDto dto) {
        if (status == null) {
            throw new IllegalArgumentException("The status cannot be empty!");
        } else if (status.trim().isEmpty()) {
            throw new IllegalArgumentException("The status cannot be empty!");
        }

        Optional<Course> course = courseRepository.findByName(dto.name());

        if (course.isPresent()) {
            Long id = course.get().getId();
            log.info("Updating status of course with id: {}", id);
            courseRepository.updateStatusById(status, id);
        } else {
            throw new IllegalStateException("Course not found!");
        }
    }

    @Transactional
    public Course createCourse(CourseDto dto) {
        if (dto == null) throw new IllegalArgumentException();
        if (courseRepository.existsByName(dto.name())) {
            throw new IllegalStateException("A course with this name already exists!");
        }
        log.info("Creating a new course: {}", dto);
        Course newCourse = courseMapper.convertDtoToEntity(dto);
        return courseRepository.saveAndFlush(newCourse);
    }

    public Course assignCourseToTeacher(String courseName, String teacherEmail) {
        Optional<Course> dbObjCourse = courseRepository.findByName(courseName);
        Course course = dbObjCourse.orElseThrow(() ->
                new IllegalArgumentException("Course not found!"));
        if (course.getTeacher() != null) {
            throw new IllegalArgumentException("A course is already assigned to a teacher!");
        }
        Optional<Teacher> dbObjTeacher = teacherRepository.findByEmail(teacherEmail);
        Teacher teacher = dbObjTeacher.orElseThrow(() ->
                new IllegalArgumentException("Teacher not found!"));
        course.setTeacher(teacher);
        teacher.getCourses().add(course);
        return courseRepository.saveAndFlush(course);
    }
}
