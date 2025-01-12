package com.gotinite.course_management.mappers;

import com.gotinite.course_management.dtos.CourseDto;
import com.gotinite.course_management.models.Course;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CourseMapperTest {

    private final CourseMapper underTest = Mappers.getMapper(CourseMapper.class);

    @ParameterizedTest
    @MethodSource("paramProvider")
    void convertDtoToEntityTest(CourseDto dto, String[] emptyFields) {
        Course result = underTest.convertDtoToEntity(dto);
        assertThat(result)
                .isNotNull()
                .hasNoNullFieldsOrPropertiesExcept(emptyFields)
                .extracting("name", "status")
                .containsExactly(dto.name(), dto.status());
    }

    private static Stream<Arguments> paramProvider() {
        return Stream.of(
                Arguments.of(
                        new CourseDto("Math", "ACTIVE"),
                        new String[]{"id", "enrollments", "grades", "teacher", "students"}
                ),
                Arguments.of(
                        new CourseDto("Programming with Java", "INACTIVE"),
                        new String[]{"id", "enrollments", "grades", "teacher", "students"}
                ),
                Arguments.of(
                        new CourseDto(null, "INACTIVE"),
                        new String[]{"id", "enrollments", "grades", "teacher", "students"}
                )
        );
    }
}
