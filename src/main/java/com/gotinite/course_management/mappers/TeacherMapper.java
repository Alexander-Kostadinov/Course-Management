package com.gotinite.course_management.mappers;

import com.gotinite.course_management.dtos.TeacherDto;
import com.gotinite.course_management.models.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeacherMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "grades", ignore = true)
    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "firstName", source = "dto.firstName")
    @Mapping(target = "lastName", source = "dto.lastName")
    @Mapping(target = "email", source = "dto.email")
    Teacher convertDtoToEntity(TeacherDto dto);
}
