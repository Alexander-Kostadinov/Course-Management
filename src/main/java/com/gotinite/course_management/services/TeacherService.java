package com.gotinite.course_management.services;

import com.gotinite.course_management.dtos.TeacherDto;
import com.gotinite.course_management.mappers.TeacherMapper;
import com.gotinite.course_management.models.Course;
import com.gotinite.course_management.models.Teacher;
import com.gotinite.course_management.repositories.CourseRepository;
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
public class TeacherService {

    private final CourseRepository courseRepository;
    private final TeacherMapper teacherMapper;
    private final TeacherRepository teacherRepository;

    public List<Teacher> getTeachersByFirstName(String firstName) {
        return teacherRepository.findByFirstName(firstName);
    }

    public Page<Teacher> getAllTeachers(Pageable pageable) {
        return teacherRepository.findAll(pageable);
    }

    public List<Teacher> getTeachersByLastName(String lastName) {
        return teacherRepository.findByLastName(lastName);
    }

    public List<Teacher> getTeachersByFullName(String name) {
        return teacherRepository.findByFullName(name);
    }

    public Optional<Teacher> getTeacherByCourse(String name) {
        Optional<Course> course = courseRepository.findByName(name);
        if (course.isPresent()) {
            Long id = course.get().getId();
            return teacherRepository.findByCourseId(id);
        } else {
            throw new IllegalArgumentException("Course not found!");
        }
    }

    @Transactional
    public void updateTeacherEmail(String email, TeacherDto dto) {
        if (email == null) {
            throw new IllegalArgumentException("The email cannot be empty!");
        } else if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("The email cannot be empty!");
        } else if (teacherRepository.existsByEmail(email)) {
            throw new IllegalStateException("A teacher with this email already exists!");
        }

        Optional<Teacher> teacher = teacherRepository.findByEmail(dto.email());

        if (teacher.isPresent()) {
            Long id = teacher.get().getId();
            log.info("Updating email of teacher with id: {}", id);
            teacherRepository.updateEmailById(email, id);
        } else {
            throw new IllegalStateException("Teacher not found!");
        }
    }

    @Transactional
    public Teacher createTeacher(TeacherDto dto) {
        if (dto == null) throw new IllegalArgumentException();
        else if (teacherRepository.existsByEmail(dto.email())) {
            throw new IllegalStateException("A teacher with this email already exists!");
        }
        log.info("Creating a new teacher: {}", dto);
        Teacher newTeacher = teacherMapper.convertDtoToEntity(dto);
        return teacherRepository.saveAndFlush(newTeacher);
    }
}
