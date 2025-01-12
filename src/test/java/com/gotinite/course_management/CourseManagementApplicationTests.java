package com.gotinite.course_management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CourseManagementApplicationTests {

	@Autowired
	private CourseManagementApplication underTest;

	@Test
	void contextLoads() {
		assertThat(underTest).isNotNull();
	}

}
