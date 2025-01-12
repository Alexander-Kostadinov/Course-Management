package com.gotinite.course_management.mappers;

import com.gotinite.course_management.dtos.TeacherDto;
import com.gotinite.course_management.models.Teacher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherMapperTest {

    private final TeacherMapper underTest = Mappers.getMapper(TeacherMapper.class);

    @ParameterizedTest
    @MethodSource("paramProvider")
    void convertDtoToEntityTest(TeacherDto dto, String[] emptyFields) {
        Teacher result = underTest.convertDtoToEntity(dto);

        assertThat(result)
                .isNotNull()
                .hasNoNullFieldsOrPropertiesExcept(emptyFields)
                .extracting("firstName", "lastName", "email")
                .containsExactly(dto.firstName(), dto.lastName(), dto.email());
    }

    private static Stream<Arguments> paramProvider() {
        return Stream.of(
                Arguments.of(
                        new TeacherDto("Petar", "Petrov", "petar.petrov@example.com"),
                        new String[]{"id", "grades", "courses"}
                ),
                Arguments.of(
                        new TeacherDto(null, null, "petar.petrov@example.com"),
                        new String[]{"id", "grades", "courses"}
                ),
                Arguments.of(
                        new TeacherDto("Petar", "Petrov", null),
                        new String[]{"id", "grades", "courses"}
                )
        );
    }
}
