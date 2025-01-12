package com.gotinite.course_management.mappers;

import com.gotinite.course_management.dtos.StudentDto;
import com.gotinite.course_management.models.Student;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StudentMapperTest {

    private final StudentMapper underTest = Mappers.getMapper(StudentMapper.class);

    @ParameterizedTest
    @MethodSource("paramProvider")
    void convertDtoToEntityTest(StudentDto dto, String[] emptyFields) {
        Student result = underTest.convertDtoToEntity(dto);

        assertThat(result)
                .isNotNull()
                .hasNoNullFieldsOrPropertiesExcept(emptyFields)
                .extracting("firstName", "lastName", "email")
                .containsExactly(dto.firstName(), dto.lastName(), dto.email());
    }

    private static Stream<Arguments> paramProvider() {
        return Stream.of(
                Arguments.of(
                        new StudentDto("Ivan", "Ivanov", "ivan.ivanov@example.com"),
                        new String[]{"id", "enrollments", "grades", "courses"}
                ),
                Arguments.of(
                        new StudentDto(null, "Ivanov", "ivan.ivanov@example.com"),
                        new String[]{"id", "enrollments", "grades", "courses"}
                ),
                Arguments.of(
                        new StudentDto("Ivan", null, null),
                        new String[]{"id", "enrollments", "grades", "courses"}
                )
        );
    }
}
