package org.example.hansabal.domain.dib.service.integration;

import org.example.hansabal.domain.comment.service.DibService;
import org.example.hansabal.domain.users.dto.request.UserCreateRequest;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
@Slf4j
@Sql(scripts = "/board_test_db.sql",
	executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DibTest {

	@Container
	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
		.withDatabaseName("testdb")
		.withUsername("testuser")
		.withPassword("testpass");

	@Autowired
	private DibService dibService;

	@Autowired
	private UserService userService;

	@BeforeAll
	void creatUser(){

		for(int i = 0; i < 1000; i++){
			String email = "user" + i + "@exmaple.com";
			String nickname = "nickname" + i;

			UserCreateRequest request = new UserCreateRequest(
				email,
				"@Aa123456",
				"테스트이름",
				nickname,
				UserRole.USER
			);

			userService.createUser(request);
		}
	}

	@Test
	void 좋아요_증가_테스트(){

	}
}
