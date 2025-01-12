package com.gotinite.course_management.mappers;

import com.gotinite.course_management.dtos.CourseDto;
import com.gotinite.course_management.models.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "grades", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "status", source = "dto.status")
    Course convertDtoToEntity(CourseDto dto);
}
